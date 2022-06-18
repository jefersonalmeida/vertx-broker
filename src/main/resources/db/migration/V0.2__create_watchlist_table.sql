create table watchlist
(
  account_id varchar,
  asset      varchar,
  foreign key (asset) references broker.assets (value),
  primary key (account_id, asset)
);
