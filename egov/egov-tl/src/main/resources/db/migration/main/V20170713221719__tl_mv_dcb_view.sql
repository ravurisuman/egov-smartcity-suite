
DROP VIEW egtl_mv_dcb_view;

CREATE OR REPLACE VIEW egtl_mv_dcb_view AS 
 SELECT mv.licensenumber,
    mv.licaddress,
    mv.licenseid,
    mv.username,
    mv.wardid,
    mv.active,
    mv.locality,
    sum(mv.curr_demand) AS curr_demand,
    sum(mv.curr_coll) AS curr_coll,
    sum(mv.curr_balance) AS curr_balance,
    sum(mv.arr_demand) AS arr_demand,
    sum(mv.arr_coll) AS arr_coll,
    sum(mv.arr_balance) AS arr_balance
   FROM ( SELECT lic.license_number AS licensenumber,
            lic.address AS licaddress,
            lic.id AS licenseid,
            licadd.applicant_name AS username,
            lic.id_parent_bndry AS wardid,
            lic.id_adm_bndry AS locality,
            lic.is_active AS active,
            currdd.amount AS curr_demand,
            currdd.amt_collected AS curr_coll,
            currdd.amount - currdd.amt_collected AS curr_balance,
            COALESCE(0, 0) AS arr_demand,
            COALESCE(0, 0) AS arr_coll,
            COALESCE(0, 0) AS arr_balance,
            im.id AS id_installment
           FROM egtl_license lic
             JOIN egtl_licensee licadd ON licadd.id_license = lic.id
             JOIN egtl_mstr_status licst ON licst.id = lic.id_status
             JOIN eg_demand currdmd ON currdmd.id = lic.id_demand
             JOIN eg_boundary bnd ON lic.id_adm_bndry = bnd.id
             LEFT JOIN eg_demand_details currdd ON currdd.id_demand = currdmd.id
             LEFT JOIN eg_demand_reason dr ON dr.id = currdd.id_demand_reason
             LEFT JOIN eg_demand_reason_master drm ON drm.id = dr.id_demand_reason_master
             LEFT JOIN eg_installment_master im ON im.id = dr.id_installment
             LEFT JOIN eg_module m ON m.id = im.id_module
          WHERE drm.isdemand = true AND im.start_date = to_date('01/04/'::text || date_part('YEAR'::text, now()), 'DD/MM/YYYY'::text) AND m.name::text = 'Trade License'::text AND im.installment_type::text = 'Yearly'::text
        UNION
         SELECT lic.license_number AS licensenumber,
            lic.address,
            lic.id AS licenseid,
            licadd.applicant_name AS username,
            lic.id_parent_bndry AS wardid,
            lic.id_adm_bndry AS locality,
            lic.is_active AS active,
            COALESCE(0, 0) AS curr_demand,
            COALESCE(0, 0) AS curr_coll,
            COALESCE(0, 0) AS curr_balance,
            arrdd.amount AS arr_demand,
            arrdd.amt_collected AS arr_coll,
            arrdd.amount - arrdd.amt_collected AS arr_balance,
            im.id AS id_installment
           FROM egtl_license lic
             JOIN egtl_licensee licadd ON licadd.id_license = lic.id
             JOIN egtl_mstr_status licst ON licst.id = lic.id_status
             JOIN eg_demand arrdmd ON arrdmd.id = lic.id_demand
             JOIN eg_boundary bnd ON lic.id_adm_bndry = bnd.id
             LEFT JOIN eg_demand_details arrdd ON arrdd.id_demand = arrdmd.id
             LEFT JOIN eg_demand_reason dr ON dr.id = arrdd.id_demand_reason
             LEFT JOIN eg_demand_reason_master drm ON drm.id = dr.id_demand_reason_master
             LEFT JOIN eg_installment_master im ON im.id = dr.id_installment
             LEFT JOIN eg_module m ON m.id = im.id_module
          WHERE drm.isdemand = true AND im.start_date <> to_date('01/04/'::text || date_part('YEAR'::text, now()), 'DD/MM/YYYY'::text) AND m.name::text = 'Trade License'::text AND im.installment_type::text = 'Yearly'::text) mv
  GROUP BY mv.licensenumber, mv.licaddress, mv.licenseid, mv.username, mv.wardid, mv.locality, mv.active;