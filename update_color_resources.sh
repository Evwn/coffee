#!/bin/bash

# Directory containing color resources
COLOR_DIR="app/src/main/res/color"

# List of files to rename (excluding those already prefixed with coffeeshop_)
FILES_TO_RENAME=(
  "nav_item_color_state.xml"
  "selector_checkbox.xml"
  "selector_chip_icon_tint.xml"
  "selector_radio_button.xml"
  "selector_switch_thumb.xml"
  "selector_switch_track.xml"
)

# Rename each file
for file in "${FILES_TO_RENAME[@]}"; do
  if [ -f "$COLOR_DIR/$file" ]; then
    # Rename with coffeeshop_ prefix
    new_name="coffeeshop_${file}"
    # Update the file content to use prefixed color resources
    sed -i 's/color="@color\//color="@color\/coffeeshop_/g' "$COLOR_DIR/$file"
    mv "$COLOR_DIR/$file" "$COLOR_DIR/$new_name"
    echo "Updated and renamed: $file -> $new_name"
  else
    echo "Warning: File $file not found in $COLOR_DIR"
  fi
done

echo "Done updating color resources."
