CREATE INDEX ix_product_colors_product_main ON product_colors(product_id, is_main);
CREATE INDEX ix_pci_color_main ON product_color_images(color_id, is_main);
CREATE INDEX ix_pci_color_sort ON product_color_images(color_id, sort_order);
CREATE INDEX ix_product_colors_product_family ON product_colors(product_id, color_family);
