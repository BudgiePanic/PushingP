/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.toy;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.raster.CanvasLineDrawer;

public class LineDrawerDemo implements Runnable {

    @Override
    public void run() {
        final int size = 256;
        ArrayCanvas canvas = new ArrayCanvas(size, size);
        CanvasLineDrawer.drawLine(0, 0, size-1, size-1, canvas, Colors.white);
        CanvasLineDrawer.drawLine(size-1, 0, 0, size-1, canvas, Colors.white);
        CanvasLineDrawer.drawLine(size/2, 0, size/2, size-1, canvas, Colors.white);
        CanvasLineDrawer.drawLine(0, size/2, size-1, size/2, canvas, Colors.white);
        CanvasWriter.saveImageToFile(canvas, "lines.ppm");
    }
    
}
