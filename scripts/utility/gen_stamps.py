# Note: imagemagickv7 is required to run the program
from typing import Optional
import subprocess
import random
import csv
import os

from tqdm import tqdm

VERBOSE = False

r"""
REFERENCE COMMANDS for rectangle

magick -size 340x100 -background white -fill Navy -font Arial -pointsize 25 \
       -gravity East caption:"شركة ذات مسؤولية محدودة University of Al Falafel New South Arabia in Africa" \
       -gravity East -extent 400x120 \
       -gravity Center -extent 420x120 \
       -strokewidth 5 -stroke Navy -fill none -draw "rectangle 5,5 415,115" \
       \( stamp/logo.jpg -thumbnail x80 \) -gravity west -geometry +15+0 -composite \
       image_clean.jpg

magick image_clean.jpg \
       -blur 0x1 \( -size 420x120 xc: +noise random -channel g -separate +channel -threshold 75% -transparent black \) \
       -compose over -composite -fuzz 30% -transparent black \
       image.jpg

innc_radius = 80
midc_radius = 120
outc_radius = 130
circle_stroke = "#00289e"
img_w, img_h = 300, 300
cmd = ["magick",
       "-size", f"{img_w}x{img_h}", "canvas:white",
       "-fill", "transparent", "-stroke", circle_stroke, "-strokewidth", "3",
       "-draw", f"translate {img_w/2},{img_h/2} circle 0,0 {innc_radius},0",
       "-draw", f"translate {img_w/2},{img_h/2} circle 0,0 {midc_radius},0",
       "-fill", "transparent", "-stroke", circle_stroke, "-strokewidth", "5",
       "-draw", f"translate {img_w/2},{img_h/2} circle 0,0 {outc_radius},0",
       "circle.jpg"]

convert -size 300x300 canvas:white -fill transparent \
        -stroke blue -strokewidth 3 -draw 'translate 150,150 circle 0,0 10,0' -draw 'translate 150,150 circle 0,0 20,0' \
        -fill transparent -stroke blue -strokewidth 5 -draw 'translate 150.0,150.0 circle 0,0 24,0' \
        img.jpg
"""


def gen_stamp(
    label: str,
    save_path: str = "stamp.jpg",
    city: Optional[str] = None,
    pscode: Optional[str] = None,
    add_rotation: bool = True,
):
    """
    label text with len > ROUND_STAMP_FIXED_LEN will be usd to create rectangular stamps
        otherwise, round stamps are created
    ROUND_STAMP_FIXED_LEN was determined to work best for visualization
    """
    ROUND_STAMP_FIXED_LEN = 37
    STROKE_CAND = [
        "#00289e",
        "#3865b0",
        "#6663a2",
        "#3c6eec",
        "#4274b9",
        "#1e53e7",
    ]
    FONT_CAND = ["Arial"]

    orig_label = label.replace(".", "").replace("_", " ").replace(",", "")
    label = orig_label[:ROUND_STAMP_FIXED_LEN]
    fuzz = random.choice([f for f in range(20, 40)])
    threshold = random.choice([f for f in range(75, 85)])
    global_font = random.choice(FONT_CAND)
    global_stroke = random.choice(STROKE_CAND)

    # padding and round stamps
    diff = ROUND_STAMP_FIXED_LEN - len(label)
    if diff:
        if diff > 15:
            name_suffix = " Company  L.L.C."
            label += name_suffix
            diff -= len(name_suffix)
        elif diff > 8:
            name_suffix = " Company"
            label += name_suffix
            diff -= len(name_suffix)
        label = " " * (diff // 2) + label + " " * (diff // 2)
    ar_label = "بُو ظَبْيٍ"  # should be of len 11
    label += ar_label

    font_pointsize = 30
    font_fill = global_stroke
    font_stroke = global_stroke
    font_strokewidth = 1
    bgcolor = "white"
    dist_type = "Arc"
    dist_deg = 360
    rot_deg = 90
    tmp_clean_path = "_stamp_clean.jpg"
    tmp_noisy_path = "_stamp_noisy.jpg"
    blur_lvl = 1

    round_text_cmd = [
        "magick",
        "-font",
        global_font,
        "-pointsize",
        f"{font_pointsize}",
        "-fill",
        font_fill,
        "-stroke",
        font_stroke,
        "-strokewidth",
        f"{font_strokewidth}",
        f"label:{label}",
        "-virtual-pixel",
        "Background",
        "-background",
        bgcolor,
        "-distort",
        dist_type,
        f"{dist_deg}",
        "-rotate",
        f"-{rot_deg}",
        "-blur",
        f"0x{blur_lvl}",
        tmp_clean_path,
    ]

    stdout, stderr = subprocess.Popen(
        round_text_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE
    ).communicate()
    if VERBOSE:
        print(stdout, stderr)

    # get orig image width and height to place circle on center
    img_wh_cmd = f'magick {tmp_clean_path} -format "%w_%h" info:'
    stdout, stderr = subprocess.Popen(
        img_wh_cmd.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE
    ).communicate()
    if VERBOSE:
        print(stdout, stderr)
    stdout = stdout.decode("utf-8").replace('"', "").split("_")
    img_w, img_h = map(int, stdout)

    res_buffer = 50
    img_w += res_buffer
    img_h += res_buffer

    # extend image size by res_buffer
    extend_bg_cmd = f"magick {tmp_clean_path} -gravity center -extent {img_w}x{img_h} {tmp_clean_path}"
    stdout, stderr = subprocess.Popen(
        extend_bg_cmd.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE
    ).communicate()
    if VERBOSE:
        print(stdout, stderr)

    # inner and outer circle radii divisors must be based on label length
    # hardcoded parameters, determined to look the best visually
    label_len = len(label)
    innc_radius = (12.0 * img_w) / label_len
    midc_radius = (19.2 * img_w) / label_len
    outc_radius = (21.2 * img_w) / label_len

    circle_stroke = global_stroke
    # add concentric circles
    conc_circle_cmd = [
        "magick",
        tmp_clean_path,
        "-fill",
        "transparent",
        "-stroke",
        circle_stroke,
        "-strokewidth",
        "3",
        "-draw",
        f"translate {img_w / 2},{img_h / 2} circle 0,0 {innc_radius},0",
        "-draw",
        f"translate {img_w / 2},{img_h / 2} circle 0,0 {midc_radius},0",
        "-fill",
        "transparent",
        "-stroke",
        circle_stroke,
        "-strokewidth",
        "5",
        "-draw",
        f"translate {img_w / 2},{img_h / 2} circle 0,0 {outc_radius},0",
        "-blur",
        f"0x{blur_lvl}",
        tmp_noisy_path,
    ]

    stdout, stderr = subprocess.Popen(
        conc_circle_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE
    ).communicate()
    if VERBOSE:
        print(stdout, stderr)

    loc_choices = ["Abu Dhabi", "Dubai", "Sharjah", "Al Ain"]
    city = random.choice(loc_choices) if not city else city
    rand_pscode = "".join([random.choice("0123456789") for _ in range(4)])
    pscode = rand_pscode if not pscode else pscode
    if random.random() < 0.8:
        center_text1 = f"{city}, UAE"
        center_text2 = f"PO. BOX {pscode}"
        psize1 = 18
        psize2 = 16
    else:
        center_text1 = "Finance"
        center_text2 = "Department"
        psize1 = 18
        psize2 = 18
    # add center text
    center_text_cmd = [
        "magick",
        tmp_noisy_path,
        "-font",
        global_font,
        "-pointsize",
        f"{psize1}",
        "-draw",
        f"gravity center fill {global_stroke} text 0,-5 '{center_text1}'",
        "-font",
        global_font,
        "-pointsize",
        f"{psize2}",
        "-draw",
        f"gravity center fill {global_stroke} text 0,20 '{center_text2}'",
        "-blur",
        f"0x{blur_lvl}",
        tmp_noisy_path,
    ]
    stdout, stderr = subprocess.Popen(
        center_text_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE
    ).communicate()
    if VERBOSE:
        print(stdout, stderr)

    if add_rotation:
        rot_angle = random.random() * 90
    else:
        rot_angle = 0.0

    add_noise_cmd = (
        f"magick {tmp_noisy_path}",
        f"( -size {img_w}x{img_h} xc: +noise random -channel g -separate +channel -threshold {threshold}% -transparent black )",
        f"-compose over -composite -fuzz {fuzz}% -transparent black -rotate {rot_angle} {save_path}",
    )
    add_noise_cmd = " ".join(add_noise_cmd)
    stdout, stderr = subprocess.Popen(
        add_noise_cmd.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE
    ).communicate()
    if VERBOSE:
        print(stdout, stderr)

    # truncation and rectangular stamps
    if len(orig_label) >= ROUND_STAMP_FIXED_LEN + 5:
        embed_img_path = save_path
        init_size = "340x120"
        inter_size = "400x120"
        final_size = "420x120"
        bgcolor = "white"
        blur_lvl = 1
        font_pointsize = 25
        tmp_clean_path = "_stamp_clean.jpg"
        ar_label = "شركة ذات مسؤولية محدو  "
        label = ar_label + orig_label

        fw, fh = map(int, final_size.split("x"))
        rect_size = f"5,5 {fw - 5},{fh - 5}"
        font_fill = global_stroke
        font_stroke = global_stroke

        clean_rect_cmd = [
            "magick",
            "-size",
            init_size,
            "-background",
            bgcolor,
            "-fill",
            font_fill,
            "-font",
            global_font,
            "-pointsize",
            f"{font_pointsize}",
            "-gravity",
            "East",
            f"caption:{label}",
            "-gravity",
            "East",
            "-extent",
            inter_size,
            "-gravity",
            "Center",
            "-extent",
            final_size,
            "-stroke",
            global_stroke,
            "-strokewidth",
            "5",
            "-fill",
            "none",
            "-draw",
            f"rectangle {rect_size}",
            "(",
            embed_img_path,
            "-thumbnail",
            "x80",
            ")",
            "-gravity",
            "West",
            "-geometry",
            "+15+0",
            "-composite",
            tmp_clean_path,
        ]

        stdout, stderr = subprocess.Popen(
            clean_rect_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE
        ).communicate()
        if VERBOSE:
            print(stdout, stderr)

        save_path = save_path.split("/")
        fname, fext = save_path[-1].split(".")
        save_path[-1] = fname + "_rect." + fext
        save_path = "/".join(save_path)
        noisy_rect_cmd = [
            "magick",
            tmp_clean_path,
            "-blur",
            f"0x{blur_lvl}",
            "(",
            "-size",
            final_size,
            "xc:",
            "+noise",
            "random",
            "-channel",
            "g",
            "-separate",
            "+channel",
            "-threshold",
            f"{threshold}%",
            "-transparent",
            "black",
            ")",
            "-compose",
            "over",
            "-composite",
            "-fuzz",
            f"{fuzz}%",
            "-transparent",
            "black",
            save_path,
        ]
        stdout, stderr = subprocess.Popen(
            noisy_rect_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE
        ).communicate()
        if VERBOSE:
            print(stdout, stderr)


csv_root = "src/main/resources/common/company/companies_ae_en.csv"
name_city_pscode_list = []

with open(csv_root, "r") as f:
    csv_reader = csv.reader(f, delimiter=";")
    header = next(csv_reader)
    for row in csv_reader:
        name, dom, industry, country, city, ad1, ad2, pscode = row
        name_city_pscode_list.append([name, city, pscode])


ext_set = set([".png", ".jpg", ".jpeg"])
save_dir = "stamp"
os.makedirs(save_dir, exist_ok=True)

for name, city, pscode in tqdm(name_city_pscode_list):
    name = name.replace(" ", "_").replace(".", "")
    save_path = os.path.join(save_dir, name + ".jpg")
    if VERBOSE:
        print(f"Saving to {save_path}")

    gen_stamp(name, save_path, city, pscode, add_rotation=False)
