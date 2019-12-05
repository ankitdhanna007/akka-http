CREATE TABLE orders (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_id int(11) NOT NULL,
  product_id int(11) NOT NULL,
  date timestamp NULL DEFAULT NULL,
  state char(2) NOT NULL,
  is_fulfilled char(1) DEFAULT NULL,
  is_paid char(1) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;