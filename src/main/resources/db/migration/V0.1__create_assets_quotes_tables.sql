create table assets
(
  value varchar primary key
);

create table quotes
(
  id         serial primary key,
  bid        numeric,
  ask        numeric,
  last_price numeric,
  volume     numeric,
  asset      varchar,
  foreign key (asset) references assets (value),
  constraint last_price_is_positive check (last_price > 0),
  constraint volume_is_positive_or_zero check (volume >= 0)
);
