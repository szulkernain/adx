DELETE FROM vehicles;
DELETE FROM user_vehicles;


insert into users (id, first_name, email_address,password)
values ('eeb8f72a-4e3a-4475-8a37-edcaa64d53ff','Satish','awesome@awesome.org','awesome');

insert into users (id, first_name, email_address,password)
values ('632cf295-1a40-4537-8e97-530d3cbdecc3','Zombie','hero@awesome.org','herohero');

insert into vehicles (id, vin, make, model)
values ('d98c2470-ab43-4076-b058-5f97c3656fcc','JF2SHADC3DG417185','Honda','Shadow');
insert into user_vehicles (id,user_id,vehicle_id, license_plate)
values ('743e5688-fb92-4a43-9e38-e401931d40c2',
        'eeb8f72a-4e3a-4475-8a37-edcaa64d53ff',
        'd98c2470-ab43-4076-b058-5f97c3656fcc','AWESOME');


insert into vehicles (id, vin, make, model)
values ('03e587f8-7c64-4ff6-a4f5-cca8a1ba1f58','JT2BG22K6W0242999','Audi','S5');
insert into user_vehicles (id,user_id,vehicle_id, license_plate)
values ('f02eff12-d4d0-4457-98bf-804fde81ed2a',
        'eeb8f72a-4e3a-4475-8a37-edcaa64d53ff',
        '03e587f8-7c64-4ff6-a4f5-cca8a1ba1f58','BELBURN');

insert into vehicles (id, vin, make, model)
values ('57b80565-1452-43e3-8687-6a4141ee66ae','1HGEM21292L047875','Chevrolet','Equinox');
insert into user_vehicles (id,user_id,vehicle_id, license_plate)
values ('738d6bfa-b16d-4bd8-a27f-ce2616a20024',
        '632cf295-1a40-4537-8e97-530d3cbdecc3',
        '57b80565-1452-43e3-8687-6a4141ee66ae','WNDRFUL');
