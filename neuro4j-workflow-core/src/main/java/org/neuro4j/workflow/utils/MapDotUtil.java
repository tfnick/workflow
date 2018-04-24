package org.neuro4j.workflow.utils;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nick
 * @since 2015年12月28日 下午10:31:21
 * @description
 */
public class MapDotUtil {

	public static Object drill(Map map, String dotProp) {
		if (map == null || dotProp == null) {
			return null;
		}
		int p = dotProp.indexOf(".");
		String key = null;
		if (p >= 0) {
			key = dotProp.substring(0, p);
		} else {
			key = dotProp;
			return map.get(key);
		}
		String[] sa = dotProp.split("\\.");
		return _drill(map, sa, 0);
	}

	private static Object _drill(Map map, String[] sa, int ind) {
		if(map==null)return null;
		String key = sa[ind];
		if (ind == sa.length - 1) {
			return map.get(key);
		} else {
			Map m = (Map) map.get(key);
			return _drill(m, sa, ind + 1);
		}
	}

	private static Object _drill(Object parent, String[] sa, int ind) {
		if(parent==null)return null;
		String key = sa[ind];
		if (ind == sa.length - 1) {
			Object child = null;
			try {
				child = PropertyUtils.getProperty(parent, key);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return child;
		} else {
			Object child = null;
			try {
				child = PropertyUtils.getProperty(parent, key);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			if (child == null) {
				return null;
			} else {
				return _drill(child, sa, ind + 1);
			}
		}
	}

	public static Object drillBean(Object parent, String dotProp) {
		if (parent == null || dotProp == null) {
			return null;
		}
		int p = dotProp.indexOf(".");
		String key = null;
		if (p >= 0) {
			key = dotProp.substring(0, p);
		} else {
			key = dotProp;
			Object child = null;
			try {
				child = PropertyUtils.getProperty(parent, key);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return child;
		}
		String[] sa = dotProp.split("\\.");
		return _drill(parent, sa, 0);
	}

	public static void main(String[] args) {
		Map m1 = new HashMap<String, Object>();
		Map m2 = new HashMap<String, Object>();
		m2.put("b", "b4");
		m1.put("a", m2);


		System.out.println(drill(m1, "a.b"));
		System.out.println(drillBean(m1, "a.b"));
	}

}
