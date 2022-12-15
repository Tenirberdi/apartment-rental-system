INSERT INTO `roles`(`id`,`role`) VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER');
INSERT INTO `users`( `contact_info`, `email`, `enabled`, `full_name`, `pass`, `photo_name`, `registration_date`, `username`, `role_id`) VALUES
    ('telegram: @AdminGuy0', 'admin@gmail.com', 1, 'Tek Kom', '$2a$12$JU.tNMjnM7x2.yGjwcRoKu6g13SdfMOQeIilwsbZu2dngBmsi0SdW', null, '2022-01-01', 'admin', 1),
    ('telegram: @StudyGuy0', 'tekukambarov@gmail.com', 1, 'Tek Kam', '$2a$12$v3WiRe44I/ZlUD9tY.v8EuUIxlPBDt8WmWQ8KfnNyKE.KAJzz2R9a', null, '2022-01-01', 'tek', 2);
INSERT INTO `promotion_types`(`name`, `ordered`, `price`) VALUES ('VIP', 3, 20);
INSERT INTO `house_types`( `type` ) values ('Family House'), ('Town House');
INSERT INTO `ads`( `area`, `available`, `bath_room_amount`, `bed_room_amount`, `date_of_posting`, `description`, `enabled`, `furniture`,
                   `kitchen_room_amount`, `location`, `price_per_month`, `title`, `total_room_amount`, `which_floor`, `house_type_id`,
                   `renter_id`, `promotion_type_id`) VALUES
    (40, 1, 1, 1, '2022-01-01', 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Don',
     1, 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean', 1,
     'Bishkek, ahunbaeva 23', 400, 'Big New Family House', 4, 1, 1, 2, null), (40, 1, 1, 1, '2022-01-01', 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Don',
                                                                               1, 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean', 1,
                                                                               'Bishkek, ahunbaeva 23', 400, 'Town House', 4, 1, 1, 2, null);
