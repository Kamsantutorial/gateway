INSERT INTO public.security_route (id, path, uri, method, authority, "order", type)
VALUES (1, '/user-service/**', 'http://localhost:8081', NULL, NULL, 1, 'ROUTE');
INSERT INTO public.security_route (id, path, uri, method, authority, "order", type)
VALUES (3, '/user-service/api/users/find-one/**', 'http://localhost:8081', 'GET', 'ROLE_ADMIN, ROLE_USER, TEST', 1,
        NULL);
INSERT INTO public.security_route (id, path, uri, method, authority, "order", type)
VALUES (2, '/test-service/**', 'http://localhost:8082', 'GET', NULL, 1, 'ROUTE');
