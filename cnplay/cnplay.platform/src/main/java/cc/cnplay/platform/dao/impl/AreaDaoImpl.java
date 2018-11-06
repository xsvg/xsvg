package cc.cnplay.platform.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cc.cnplay.core.dao.GenericDaoImpl;
import cc.cnplay.core.util.Converter;
import cc.cnplay.platform.dao.AreaDao;
import cc.cnplay.platform.domain.Area;
import cc.cnplay.platform.domain.Status;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

@Repository
public class AreaDaoImpl extends GenericDaoImpl<Area, String> implements AreaDao
{

	@Override
	public Area getByLevelCode(String levelCode)
	{

		return this.getByField("levelCode", levelCode);
	}

	@Override
	public String getLevelCodeById(String id)
	{
		Area org = getById(id);
		String levelCode = "";
		if (org != null)
		{
			levelCode = org.getLevelCode();
		}
		return levelCode;
	}

	@Override
	public Area getRoot()
	{
		List<Area> list = findByParentId("");
		if (list.size() > 0)
		{
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<Area> findByParentId(String parentId)
	{
		Search search = new Search(Area.class);
		if (StringUtils.isEmpty(parentId))
		{
			search.addFilter(Filter.or(Filter.isEmpty("parentId"), Filter.or(Filter.equal("parentId", "0"))));
		}
		else
		{
			search.addFilterEqual("parentId", parentId);
		}
		search.addFilterEqual("status", Status.Normal);
		search.addSortAsc("code");
		@SuppressWarnings("unchecked")
		List<Area> list = (List<Area>) this.search(search);
		return list;
	}

	/**
	 * 生成下一个机构层次码
	 * 
	 * @param parentId
	 * @return
	 */
	@Override
	public String getNextLevelCodeByParent(String parentId)
	{
		String nextLevelCode = "";
		Search search = new Search(Area.class);
		if (StringUtils.isEmpty(parentId))
		{
			search.addFilter(Filter.or(Filter.isEmpty("parentId"), Filter.or(Filter.equal("parentId", "0"))));
		}
		else
		{
			search.addFilterEqual("parentId", parentId);
		}
		search.addSortDesc("levelCode");
		search.setMaxResults(1);
		Area org = (Area) searchUnique(search);
		Area parent = getById(Area.class, parentId);
		String parentLevelCode = Area.LevelCode;
		if (org == null)
		{
			if (parent == null)
			{
				nextLevelCode = Area.LevelCode;
			}
			else
			{
				parentLevelCode = parent.getLevelCode();
				nextLevelCode = parentLevelCode + Area.LevelCode;
			}
		}
		else
		{
			String lastLevelCode = org.getLevelCode();
			if (StringUtils.isEmpty(lastLevelCode))
			{
				parent = getById(Area.class, parentId);
				if (parent == null)
				{
					lastLevelCode = Area.LevelCode;
				}
				else
				{
					parentLevelCode = parent.getLevelCode();
					lastLevelCode = parentLevelCode + Area.LevelCode;
				}
			}
			nextLevelCode = Converter.nextNumber(lastLevelCode);
		}
		while (exitLevelCode(nextLevelCode))
		{
			nextLevelCode = Converter.nextNumber(nextLevelCode);
		}
		return nextLevelCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Area> findByStartsWithLevelCode(String levelCode)
	{
		if (levelCode == null)
		{
			levelCode = "";
		}
		Search search = new Search(Area.class);
		search.addFilterLike("levelCode", levelCode + "%");
		search.addSortAsc("levelCode");
		return search(search);
	}

	@Override
	public boolean exitName(Area area)
	{
		Search search = new Search(Area.class);
		search.addFilterNotEqual("id", area.getId());
		search.addFilterEqual("name", area.getName());
		search.addFilterEqual("status", Status.Normal);
		search.setMaxResults(1);
		Area a = (Area) searchUnique(search);
		return a != null;
	}

	@Override
	public boolean exitCode(Area area)
	{
		Search search = new Search(Area.class);
		search.addFilterNotEqual("id", area.getId());
		search.addFilterEqual("code", area.getCode());
		search.addFilterEqual("status", Status.Normal);
		search.setMaxResults(1);
		Area a = (Area) searchUnique(search);
		return a != null;
	}

	@Override
	public boolean exitLevelCode(String levelCode)
	{
		Search search = new Search(Area.class);
		search.addFilterEqual("levelCode", levelCode);
		search.setMaxResults(1);
		Area area = (Area) searchUnique(search);
		return area != null;
	}

}
