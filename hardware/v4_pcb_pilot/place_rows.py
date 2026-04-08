#!/usr/bin/env python3
"""
place_rows.py — position LED1..LED24 footprints in nanosun-pcb.kicad_pcb to
form the 150 mm pixel circle from sunsetlamp-pixel-circle-spec.md §3.2.

Geometry:
  - Row pitch 6.25 mm. Row r (1..24) center Y = (r - 12.5) * 6.25
    (row 1 = -71.875 mm at top, row 24 = +71.875 mm at bottom).
  - Each row footprint origin = LEFT endpoint cluster (X=0).
    Strip extends to +X by length L = pixels * 6.25 mm.
  - Serpentine (spec §6.1):
        odd rows  (1,3,5,...): DIN on the LEFT  → no rotation,
                                 footprint X = CENTER_X - L/2
        even rows (2,4,6,...): DIN on the RIGHT → rotate 180°,
                                 footprint X = CENTER_X + L/2
  - All rows centered on (CENTER_X, CENTER_Y).
"""

import re
import shutil
from pathlib import Path

PCB = Path(__file__).parent / "nanosun-pcb" / "nanosun-pcb.kicad_pcb"
BACKUP = PCB.with_suffix(".kicad_pcb.preplace")

PITCH = 6.25
CENTER_X = 150.0  # mm, somewhere clean inside default A4 page
CENTER_Y = 100.0

ROW_PX = {
    1: 6,   2: 12,  3: 14,  4: 16,  5: 18,  6: 20,  7: 22,  8: 22,
    9: 22, 10: 24, 11: 24, 12: 24, 13: 24, 14: 24, 15: 24, 16: 22,
   17: 22, 18: 22, 19: 20, 20: 18, 21: 16, 22: 14, 23: 12, 24: 6,
}


def target_xy_rot(row):
    L = ROW_PX[row] * PITCH
    y = CENTER_Y + (row - 12.5) * PITCH
    if row % 2 == 1:  # odd → DIN-left, no rotation
        return CENTER_X - L / 2, y, 0
    else:             # even → DIN-right, rotated 180°
        return CENTER_X + L / 2, y, 180


def find_footprint_block(txt, ref):
    """Return (start, end) of the (footprint ...) block whose Reference is `ref`."""
    marker = f'(property "Reference" "{ref}"'
    idx = txt.find(marker)
    if idx == -1:
        return None
    start = txt.rfind("(footprint ", 0, idx)
    if start == -1:
        return None
    depth = 0
    i = start
    while i < len(txt):
        c = txt[i]
        if c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
            if depth == 0:
                return start, i + 1
        i += 1
    return None


def set_position(block, x, y, rot):
    # The footprint's own (at X Y) or (at X Y rot) is the FIRST (at ...)
    # inside the block, before any property/pad. Replace exactly that one.
    # Use a regex anchored after the (uuid "...") line which precedes (at ...).
    new_at = f"(at {x:.4f} {y:.4f} {rot})" if rot else f"(at {x:.4f} {y:.4f})"
    pat = re.compile(r'(\(uuid "[0-9a-f-]+"\)\s*)\(at [^)]*\)')
    new_block, n = pat.subn(lambda m: m.group(1) + new_at, block, count=1)
    if n == 0:
        # fallback: replace the first (at ...) right after the (footprint header
        pat2 = re.compile(r'\(at [^)]*\)')
        new_block, n = pat2.subn(new_at, block, count=1)
    return new_block, n


def main():
    txt = PCB.read_text()
    if not BACKUP.exists():
        shutil.copy2(PCB, BACKUP)
        print(f"backup: {BACKUP.name}")

    placed = 0
    for row in range(1, 25):
        ref = f"LED{row}"
        span = find_footprint_block(txt, ref)
        if span is None:
            print(f"  ! {ref}: footprint not found in PCB")
            continue
        start, end = span
        block = txt[start:end]
        x, y, rot = target_xy_rot(row)
        new_block, n = set_position(block, x, y, rot)
        if n == 0:
            print(f"  ! {ref}: could not rewrite (at ...)")
            continue
        txt = txt[:start] + new_block + txt[end:]
        placed += 1
        print(f"  {ref}: x={x:7.3f} y={y:7.3f} rot={rot:>3}  L={ROW_PX[row]*PITCH:.2f}mm")

    PCB.write_text(txt)
    print(f"placed {placed}/24 rows")


if __name__ == "__main__":
    main()
