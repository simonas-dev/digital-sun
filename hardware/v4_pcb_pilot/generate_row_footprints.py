#!/usr/bin/env python3
"""
generate_row_footprints.py — Option A: one footprint per distinct row length.

Creates 8 .kicad_mod files in nanosun-pcb/nanosun-pcb.pretty/, one per row length
in the spec (6, 12, 14, 16, 18, 20, 22, 24 px). Each footprint has identical
endpoint clusters; only the X-distance between LEFT and RIGHT clusters varies.

Pad numbering matches the WS2812B_Strip symbol:
    1 = VCC, 2 = DIN, 3 = DOUT, 4 = GND
LEFT cluster (origin):  pads 1, 2, 4
RIGHT cluster (X=L):    pads 1, 3, 4
Same pad numbers on left/right → KiCad treats them as the same net (VCC, GND).

Geometry:
    Strip runs along +X. Width 5 mm in Y.
    Pads 2.5 mm (X, along strip) × 1.3 mm (Y, across width), 1.45 mm Y pitch.
    Pad row order across width:  Y=-1.45 VCC, Y=0 DATA, Y=+1.45 GND.

Then patches nanosun-pcb.kicad_sch to set each LED1..LED24 Footprint property
to its matching row footprint per spec §3.2 row mapping.
"""

from pathlib import Path
import re
import shutil

ROOT = Path(__file__).parent
PRETTY = ROOT / "nanosun-pcb" / "nanosun-pcb.pretty"
SCH = ROOT / "nanosun-pcb" / "nanosun-pcb.kicad_sch"

PITCH = 6.25  # mm per pixel

# px → row length in mm
LENGTHS_PX = [6, 12, 14, 16, 18, 20, 22, 24]

# Spec §3.2 row→pixel mapping (LED1 = row 1 top, LED24 = row 24 bottom)
ROW_PX = {
    1: 6,   2: 12,  3: 14,  4: 16,  5: 18,  6: 20,  7: 22,  8: 22,
    9: 22, 10: 24, 11: 24, 12: 24, 13: 24, 14: 24, 15: 24, 16: 22,
   17: 22, 18: 22, 19: 20, 20: 18, 21: 16, 22: 14, 23: 12, 24: 6,
}


def fp_name(px):
    mm = px * PITCH
    return f"WS2812B_Row_{px:02d}px_{int(mm*10):04d}_{int((mm*10)%10)}"


# nicer name: WS2812B_Row_24px_150mm
def fp_name(px):
    mm = px * PITCH
    mm_str = f"{mm:.2f}".rstrip("0").rstrip(".").replace(".", "_")
    return f"WS2812B_Row_{px:02d}px_{mm_str}mm"


def emit_footprint(px):
    L = px * PITCH
    name = fp_name(px)
    body_y = 2.5  # half strip width
    pad_size_x = 2.5
    pad_size_y = 1.3
    pad_dy = 1.45  # Y pitch within an end cluster
    # silkscreen body extends from x=-1.5 (left of LEFT cluster) to x=L+1.5
    silk_x0 = -1.5
    silk_x1 = L + 1.5
    silk_y0 = -body_y
    silk_y1 = +body_y

    def pad(num, x, y):
        return (
            f'  (pad "{num}" smd rect (at {x:g} {y:g}) (size {pad_size_x} {pad_size_y})\n'
            f'    (layers "F.Cu" "F.Paste" "F.Mask"))'
        )

    lines = [
        f'(footprint "{name}"',
        f'  (version 20241229)',
        f'  (generator "digital-sun-row-gen")',
        f'  (layer "F.Cu")',
        f'  (descr "WS2812B 12V row, {px} pixels, {L:.2f} mm; LEFT=VCC/DIN/GND, RIGHT=VCC/DOUT/GND")',
        f'  (tags "ws2812b led strip 12v row {px}px")',
        f'  (attr smd)',
        f'',
        f'  (property "Reference" "LED**"',
        f'    (at {L/2:g} {-body_y-1.2:g} 0)',
        f'    (layer "F.SilkS")',
        f'    (effects (font (size 0.8 0.8) (thickness 0.15))))',
        f'  (property "Value" "{name}"',
        f'    (at {L/2:g} {body_y+1.2:g} 0)',
        f'    (layer "F.Fab")',
        f'    (hide yes)',
        f'    (effects (font (size 0.8 0.8) (thickness 0.15))))',
        f'',
        pad(1, 0.0, -pad_dy),
        pad(2, 0.0,  0.0),
        pad(4, 0.0, +pad_dy),
        pad(1, L, -pad_dy),
        pad(3, L,  0.0),
        pad(4, L, +pad_dy),
        f'',
        f'  (fp_line (start {silk_x0:g} {silk_y0:g}) (end {silk_x1:g} {silk_y0:g})',
        f'    (stroke (width 0.12) (type solid)) (layer "F.SilkS"))',
        f'  (fp_line (start {silk_x1:g} {silk_y0:g}) (end {silk_x1:g} {silk_y1:g})',
        f'    (stroke (width 0.12) (type solid)) (layer "F.SilkS"))',
        f'  (fp_line (start {silk_x1:g} {silk_y1:g}) (end {silk_x0:g} {silk_y1:g})',
        f'    (stroke (width 0.12) (type solid)) (layer "F.SilkS"))',
        f'  (fp_line (start {silk_x0:g} {silk_y1:g}) (end {silk_x0:g} {silk_y0:g})',
        f'    (stroke (width 0.12) (type solid)) (layer "F.SilkS"))',
        f'',
        f'  (fp_text user "VCC" (at -2.2 {-pad_dy:g}) (layer "F.SilkS")',
        f'    (effects (font (size 0.5 0.5) (thickness 0.1))))',
        f'  (fp_text user "DIN" (at -2.2 0) (layer "F.SilkS")',
        f'    (effects (font (size 0.5 0.5) (thickness 0.1))))',
        f'  (fp_text user "GND" (at -2.2 {pad_dy:g}) (layer "F.SilkS")',
        f'    (effects (font (size 0.5 0.5) (thickness 0.1))))',
        f'',
        f'  (fp_text user "VCC" (at {L+2.2:g} {-pad_dy:g}) (layer "F.SilkS")',
        f'    (effects (font (size 0.5 0.5) (thickness 0.1))))',
        f'  (fp_text user "DOUT" (at {L+2.4:g} 0) (layer "F.SilkS")',
        f'    (effects (font (size 0.5 0.5) (thickness 0.1))))',
        f'  (fp_text user "GND" (at {L+2.2:g} {pad_dy:g}) (layer "F.SilkS")',
        f'    (effects (font (size 0.5 0.5) (thickness 0.1))))',
        f')',
        f'',
    ]
    return name, "\n".join(lines)


def write_footprints():
    PRETTY.mkdir(parents=True, exist_ok=True)
    written = []
    for px in LENGTHS_PX:
        name, body = emit_footprint(px)
        path = PRETTY / f"{name}.kicad_mod"
        path.write_text(body)
        written.append(path.name)
    return written


# ---------------------------------------------------------------- schematic patch
def patch_schematic():
    txt = SCH.read_text()
    backup = SCH.with_suffix(".kicad_sch.prerowfp")
    if not backup.exists():
        shutil.copy2(SCH, backup)

    # also fix R1, C1-C3 instances which have no Footprint property at all
    PASSIVE_FP = {
        "R1": "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P10.16mm_Horizontal",
        "C1": "Capacitor_THT:CP_Radial_D8.0mm_P3.80mm",
        "C2": "Capacitor_THT:CP_Radial_D8.0mm_P3.80mm",
        "C3": "Capacitor_THT:CP_Radial_D8.0mm_P3.80mm",
    }
    for ref, fp in PASSIVE_FP.items():
        ref_marker = f'(property "Reference" "{ref}"'
        idx = txt.find(ref_marker)
        if idx == -1:
            print(f"  ! {ref} not found")
            continue
        sym_start = txt.rfind("(symbol", 0, idx)
        depth = 0
        i = sym_start
        while i < len(txt):
            c = txt[i]
            if c == "(":
                depth += 1
            elif c == ")":
                depth -= 1
                if depth == 0:
                    sym_end = i + 1
                    break
            i += 1
        block = txt[sym_start:sym_end]
        if f'"Footprint" "{fp}"' in block:
            continue
        # replace empty Footprint "" with the real value
        new_block, n = re.subn(
            r'\(property "Footprint" ""',
            f'(property "Footprint" "{fp}"',
            block,
            count=1,
        )
        if n == 0:
            print(f"  ! {ref}: empty Footprint property not found")
            continue
        txt = txt[:sym_start] + new_block + txt[sym_end:]

    patched = 0
    for ref, px in ROW_PX.items():
        fp = f"nanosun-pcb:{fp_name(px)}"
        # find the symbol block whose Reference is "LEDn", then replace its
        # Footprint "" with the row footprint. The block is delimited by the
        # outer (symbol ... ) form.
        ref_marker = f'(property "Reference" "LED{ref}"'
        idx = txt.find(ref_marker)
        if idx == -1:
            print(f"  ! LED{ref} not found")
            continue
        # walk back to enclosing (symbol
        sym_start = txt.rfind("(symbol", 0, idx)
        # find matching close
        depth = 0
        i = sym_start
        while i < len(txt):
            c = txt[i]
            if c == "(":
                depth += 1
            elif c == ")":
                depth -= 1
                if depth == 0:
                    sym_end = i + 1
                    break
            i += 1
        block = txt[sym_start:sym_end]
        # already patched?
        if f'"Footprint" "{fp}"' in block:
            continue
        # replace the first  (property "Footprint" ""  inside this block
        new_block, n = re.subn(
            r'\(property "Footprint" ""',
            f'(property "Footprint" "{fp}"',
            block,
            count=1,
        )
        if n == 0:
            print(f"  ! LED{ref}: empty Footprint property not found in block")
            continue
        txt = txt[:sym_start] + new_block + txt[sym_end:]
        patched += 1
    SCH.write_text(txt)
    return patched


def main():
    print("Generating row footprints...")
    written = write_footprints()
    for w in written:
        print(f"  + {w}")
    print(f"Patching schematic ({SCH.name})...")
    n = patch_schematic()
    print(f"  assigned footprints to {n} LED row symbols")
    print("Done. Open in KiCad, run ERC, then update PCB from schematic.")


if __name__ == "__main__":
    main()
