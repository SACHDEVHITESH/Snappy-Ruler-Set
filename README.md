# Snappy-Ruler-Set

## 🏗 Architecture Overview
**MVVM Pattern**
- `DrawingViewModel` holds all mutable state (shapes, tools, canvas transform).
- `SnappyRulerScreen` (`@Composable`) renders UI and forwards gestures to the ViewModel.
- Rendering helpers (`CanvasDrawExtensions`) handle grid, shapes, and tool visuals.

**State Flow**
- User gestures → `ViewModel` mutates `shapes/tools` → Compose recomposes → Canvas redraws.
- Undo/Redo uses stacks of immutable `List<Shape>`.

**Rendering Loop**
- Every frame, Compose reads `shapes` + `tools` and re-renders via `drawShapes` + `drawTools`.

---

## 🎯 Snapping Strategy & Data Structures
- **SnapEngine**: Central utility that aligns points to:
    - Grid intersections (nearest multiple of grid spacing).
    - Existing line endpoints or midpoints.
    - Circle centers.

- **Data Structures**
    - Simple linear search over current `shapes` (no spatial tree yet).
    - Hit-testing uses Euclidean distance with a zoom-scaled snap radius.

- **Angle Snapping**
    - Tool rotations are quantized to nearest 0°, 30°, 45°, 60°, 90°, etc. within a threshold.

---

## ⚡ Performance Notes
- Shapes are lightweight (`data class` sealed hierarchy).
- Rendering is **O(N)** per frame, sufficient for hundreds of shapes on mobile.
- Profiling shows bottleneck only when freehand paths exceed thousands of points.
- Trade-off: avoided complex spatial indexing (e.g., quadtree) for simplicity, since input scale is modest.

---

## 📏 Calibration & Real-World Units
- **Pixels per mm** (`pixelsPerMm`) default ≈ `dpi / 25.4`.
- `SnapEngine` uses this to compute grid spacing in pixels.

**Calibration Flow**
1. Optionally, user can align a drawn ruler with a physical ruler → adjust `pixelsPerMm`.
2. This ensures 1 cm on screen corresponds to 1 cm real-world.
3. Current assumption: device reports accurate DPI. Some phones may need manual correction.

---

## 🎥 Demo Video
👉 [Watch the 2-min demo video](https://drive.google.com/file/d/1VRC8ZWMV9nhD7Sooak4sYJnLnFGRkFrT/view?usp=drive_link)

[![Watch Demo](https://img.shields.io/badge/▶-Watch%20Demo-red?style=for-the-badge)](https://drive.google.com/file/d/1VRC8ZWMV9nhD7Sooak4sYJnLnFGRkFrT/view?usp=drive_link)
