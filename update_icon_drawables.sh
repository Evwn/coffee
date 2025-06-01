#!/bin/bash

drawables=(
    "coffeeshop_ic_expand_more"
    "coffeeshop_ic_orders"
    "coffeeshop_ic_search"
    "coffeeshop_ic_menu"
    "coffeeshop_ic_person"
    "coffeeshop_ic_home"
    "coffeeshop_ic_logout"
    "coffeeshop_ic_settings"
    "coffeeshop_ic_help"
    "coffeeshop_ic_payment"
    "coffeeshop_ic_info"
    "coffeeshop_ic_notifications"
)

for drawable in "${drawables[@]}"; do
    sed -i 's/fillColor=\"@color\/coffee_text_primary\"/fillColor=\"@color\/coffeeshop_coffee_text_primary\"/' "app/src/main/res/drawable/$drawable.xml"
done
