#!/bin/bash

# Directory containing layout resources
LAYOUT_DIR="app/src/main/res/layout"

# Update drawable references in layout files
find "$LAYOUT_DIR" -type f -name "*.xml" -exec sed -i 's/@drawable\//@drawable\/coffeeshop_/g' {} \;

# Update color references in layout files
find "$LAYOUT_DIR" -type f -name "*.xml" -exec sed -i 's/@color\//@color\/coffeeshop_/g' {} \;

echo "Done updating layout resource references."
