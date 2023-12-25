create table if not exists security_route
(
    id
    serial,
    path VARCHAR (128) not null,
    uri VARCHAR(128) default null,
    method VARCHAR(128) default null,
    authority VARCHAR(128) default null,
    "type" VARCHAR(128) default null,
    "order" int default null,
    primary key(id)
);

INSERT INTO public.security_route (id, path, uri, method, authority, "order", type)
VALUES (1, '/user-service/**', 'http://localhost:8081', NULL, NULL, 1, 'ROUTE');

INSERT INTO public.security_route (id, path, uri, method, authority, "order", type)
VALUES (2, '/order-service/**', 'http://localhost:8082', NULL, NULL, 1, 'ROUTE');