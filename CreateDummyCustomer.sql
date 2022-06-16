drop procedure if exists inventory.create_dummy_customers;

delimiter #

CREATE DEFINER=`root`@`%` PROCEDURE `inventory`.`create_dummy_customers`(in cnt int)
begin
	declare _cnt int;  
        set _cnt = 1;
        start transaction;
        while _cnt <= cnt do  
                insert into inventory.customers (first_name, last_name, email)
				 values('dummy first name', 'dummy last name', MD5(RAND()));
				commit;
                set _cnt = _cnt + 1;  
               do sleep(2);
              
        end while;
        commit;
end#

delimiter ;