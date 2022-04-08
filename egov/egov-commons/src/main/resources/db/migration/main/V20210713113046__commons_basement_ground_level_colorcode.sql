insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) 
select nextval('state.seq_egdcr_layername'),'LAYER_NAME_COLOUR_CODE_LEVEL_OF_BASEMENT_UNDER_GROUND','COLOUR_CODE_LEVEL_OF_BASEMENT_UNDER_GROUND',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_COLOUR_CODE_LEVEL_OF_BASEMENT_UNDER_GROUND');

select setval('generic.seq_egdcr_sub_feature_colorcode', (select max(id) from generic.egdcr_sub_feature_colorcode ));

INSERT INTO generic.egdcr_sub_feature_colorcode(id, feature, subfeature, colorcode,ordernumber) VALUES (nextval('generic.seq_egdcr_sub_feature_colorcode'), 'Basement', 'COLOUR_CODE_LEVEL_OF_BASEMENT_UNDER_GROUND', 3, 8);