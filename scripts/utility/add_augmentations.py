import cv2
import time
import random
from augraphy import AugraphyPipeline, AugmentationSequence, OneOf
# ink phase
from augraphy import Dithering, InkBleed, Letterpress, BleedThrough, LowInkPeriodicLines
# paper phase
from augraphy import PaperFactory, ColorPaper, WaterMark
from augraphy import NoiseTexturize, BrightnessTexturize
# post phase
from augraphy import DirtyRollers, DirtyDrum, SubtleNoise, Jpeg, LightingGradient
from augraphy import Markup, PencilScribbles, BadPhotoCopy, Faxify, BookBinding


ink_phase = [
    Dithering(
        dither=random.choice(["ordered", "floyd-steinberg"]),
        order=random.randint(3, 5),
        p=0.1,
    ),
    InkBleed(
        intensity_range=(0.1, 0.2),
        color_range=(0, 16),
        kernel_size=random.choice([(7, 7), (5, 5), (3, 3)]),
        severity=(0.3, 0.5),
        p=0.2,
    ),
    OneOf(
        [
            Letterpress(
                n_samples=(100, 400),
                n_clusters=(200, 400),
                std_range=(500, 3000),
                value_range=(150, 224),
                value_threshold_range=(96, 128),
                blur=1,
            ),
            # BleedThrough(
            #     intensity_range=(0.1, 0.3),
            #     color_range=(32, 224),
            #     ksize=(17, 17),
            #     sigmaX=1,
            #     alpha=random.uniform(0.1, 0.2),
            #     offsets=(10, 20),
            # ),
            # LowInkPeriodicLines(
            #     count_range=(2, 5),
            #     period_range=(16, 32),
            #     use_consistent_lines=random.choice([True, False]),
            #     noise_probability=0.1,
            # ),
        ],
    ),
]

paper_phase = [
    PaperFactory(p=0.33),
    ColorPaper(
        hue_range=(0, 255),
        saturation_range=(10, 40),
        p=0.2,
    ),
    WaterMark(
        watermark_word="random",
        watermark_font_size=(10, 15),
        watermark_font_thickness=(20, 25),
        watermark_rotation=(0, 360),
        watermark_location="random",
        watermark_color="random",
        watermark_method="darken",
        p=0.05,
    ),
    AugmentationSequence(
        [
            NoiseTexturize(
                sigma_range=(3, 10),
                turbulence_range=(2, 5),
            ),
            BrightnessTexturize(
                deviation=0.03,
            ),
        ],
    ),
]

post_phase = [
    DirtyRollers(
        line_width_range=(2, 32),
        scanline_type=0,
        p=0.1,
    ),
    DirtyDrum(
        line_width_range=(1, 6),
        line_concentration=random.uniform(0.05, 0.15),
        direction=random.randint(0, 2),
        noise_intensity=random.uniform(0.6, 0.95),
        noise_value=(64, 224),
        ksize=random.choice([(3, 3), (5, 5), (7, 7)]),
        sigmaX=0,
        p=0.03,
    ),
    SubtleNoise(
        subtle_range=10,
        p=0.2,
    ),
    Jpeg(
        quality_range=(65, 95),
        p=0.33,
    ),
    LightingGradient(
        light_position=None,
        direction=random.randint(45, 90),
        max_brightness=255,
        min_brightness=0,
        mode="gaussian",
        transparency=0.3
    ),
    Markup(
        num_lines_range=(2, 7),
        markup_length_range=(0.5, 1),
        markup_thickness_range=(1, 2),
        markup_type=random.choice(
            ["strikethrough", "crossed", "highlight", "underline"]),
        markup_color="random",
        single_word_mode=False,
        repetitions=1,
        p=0.33,
    ),
    PencilScribbles(
        size_range=(100, 800),
        count_range=(1, 6),
        stroke_count_range=(1, 2),
        thickness_range=(2, 6),
        brightness_change=random.randint(64, 224),
        p=0.33,
    ),
    BadPhotoCopy(
        mask=None,
        noise_type=-1,
        noise_side="random",
        noise_iteration=(1, 2),
        noise_size=(1, 3),
        noise_value=(128, 196),
        noise_sparsity=(0.3, 0.6),
        noise_concentration=(0.1, 0.6),
        blur_noise=random.choice([True, False]),
        blur_noise_kernel=random.choice([(3, 3), (5, 5), (7, 7)]),
        wave_pattern=random.choice([True, False]),
        edge_effect=random.choice([True, False]),
        p=0.13,
    ),
    Faxify(
        scale_range=(0.3, 0.6),
        monochrome=random.choice([0, 1]),
        monochrome_method="random",
        monochrome_arguments={},
        halftone=random.choice([0, 1]),
        invert=1,
        half_kernel_size=random.choice([(1, 1), (2, 2)]),
        angle=(0, 360),
        sigma=(1, 3),
        p=0.13,
    ),
    # BookBinding(
    #     radius_range=(1, 100),
    #     mirror_range=(0.3, 0.5),
    #     p=0.33,
    # ),
]

pipeline = AugraphyPipeline(ink_phase, paper_phase, post_phase)

img = cv2.imread("target/amazon/img/amazon-1.jpg")

t0 = time.time()
data = pipeline.augment(img)
t1 = time.time()
augmented = data["output"]
t2 = time.time()

print(f"time {t2-t0}s")
cv2.imwrite("aug.jpg", augmented)
