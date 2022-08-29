USE date_calculation;

CREATE TABLE date_calculation.formula
(
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(30) NOT NULL,
    detail varchar(100),
    year int NOT NULL,
    month int NOT NULL,
    day int NOT NULL
);

INSERT INTO date_calculation.formula (name, detail, year, month, day) VALUES ("年のみ", "最大値", 100, 0, 0);
INSERT INTO date_calculation.formula (name, detail, year, month, day) VALUES ("月と日", "最小値", 0, -100, -1000);
INSERT INTO date_calculation.formula (name, detail, year, month, day) VALUES ("年を超える月", "プラス", -1, 13, 0);
INSERT INTO date_calculation.formula (name, detail, year, month, day) VALUES ("月を超える日", "マイナス", 0, 1, -31);
INSERT INTO date_calculation.formula (name, detail, year, month, day) VALUES ("閏年", "基準日が閏年", 0, 0, 1);
INSERT INTO date_calculation.formula (name, detail, year, month, day) VALUES ("閏年", "計算結果が閏年", 2, 0, 1);
