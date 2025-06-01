#!/bin/bash

# Directory containing drawable resources
DRAWABLE_DIR="app/src/main/res/drawable"

# List of files to rename (excluding those already prefixed with coffeeshop_)
FILES_TO_RENAME=(
  "divider_popup_menu.xml"
  "ic_add.xml"
  "ic_arrow_back.xml"
  "ic_dashboard_black_24dp.xml"
  "ic_expand_more.xml"
  "ic_help.xml"
  "ic_home.xml"
  "ic_home_black_24dp.xml"
  "ic_info.xml"
  "ic_launcher_background.xml"
  "ic_launcher_foreground.xml"
  "ic_logout.xml"
  "ic_menu.xml"
  "ic_notifications.xml"
  "ic_notifications_black_24dp.xml"
  "ic_orders.xml"
  "ic_payment.xml"
  "ic_person.xml"
  "ic_profile.xml"
  "ic_purchase.xml"
  "ic_remove.xml"
  "ic_search.xml"
  "ic_settings.xml"
  "ic_shopping_cart.xml"
  "search_background.xml"
  "seekbar_progress.xml"
  "seekbar_thumb.xml"
)

# Rename each file
for file in "${FILES_TO_RENAME[@]}"; do
  if [ -f "$DRAWABLE_DIR/$file" ]; then
    # Extract the base name and extension
    base_name=$(basename "$file" .xml)
    # Rename with coffeeshop_ prefix
    mv "$DRAWABLE_DIR/$file" "$DRAWABLE_DIR/coffeeshop_$file"
    echo "Renamed: $file -> coffeeshop_$file"
  else
    echo "Warning: File $file not found in $DRAWABLE_DIR"
  fi
done

echo "Done renaming drawable resources."
