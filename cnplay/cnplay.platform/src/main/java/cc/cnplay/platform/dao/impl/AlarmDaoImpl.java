package cc.cnplay.platform.dao.impl;

import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.platform.dao.AlarmDao;
import cc.cnplay.platform.domain.Alarm;

@Repository
public class AlarmDaoImpl extends GenericDaoImpl<Alarm, String> implements AlarmDao
{

}
