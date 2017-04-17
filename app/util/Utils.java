package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.mvc.Http;

public class Utils {

	public static <T> List<T> removeDuplicates(Collection<T> list, Comparator<T> comparator) {
		final Set<T> tmpSet = new TreeSet<>(comparator);
		tmpSet.addAll(list);
		return new ArrayList<>(tmpSet);
	}

	public static <T, O> List<O> mapList(List<T> inputList, Function<T, O> mapper) {
		return inputList.stream().map(mapper).collect(Collectors.toList());
	}

	public static Date getDateBefore(long seconds) {
		return new Date(new Date().getTime() - 1000L * seconds);
	}

	/**
	 * This method calculates the distance between two points (given the
	 * latitude/longitude of those points). It is being used to calculate the
	 * distance between two locations
	 * 
	 * @param latitude1
	 *            Latitude of point 1 (in decimal degrees)
	 * @param longitude1
	 *            Longitude of point 1 (in decimal degrees)
	 * @param latitude2
	 *            Latitude of point 2 (in decimal degrees)
	 * @param longitude2
	 *            Longitude of point 2 (in decimal degrees)
	 * @return distance in meters
	 */
	public static Double getDistance(String latitude1, String longitude1, String latitude2, String longitude2) {
		
		double lat1 = Double.parseDouble(latitude1);
		double lon1 = Double.parseDouble(longitude1);
		double lat2 = Double.parseDouble(latitude2);
		double lon2 = Double.parseDouble(longitude2);

		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);

		dist = dist * 60 * 1.1515 * 1.609344 * 1000;

		return dist;
	}

	private static double deg2rad(double deg) {
		return deg * Math.PI / 180.0;
	}

	private static double rad2deg(double rad) {
		return rad * 180 / Math.PI;
	}

	public static String getLoginUser() {
		try {
			String[] authTokenHeaderValues = Http.Context.current().request().headers().get("Authorization");
			if (ArrayUtils.getLength(authTokenHeaderValues) == 1) {
				String authDataEnc = StringUtils.remove(authTokenHeaderValues[0], "Basic ");
				String authDataDec = new String(Base64.getDecoder().decode(authDataEnc.getBytes()), "UTF-8");
				return StringUtils.split(authDataDec, ":")[0];
			}
		} catch (Exception e) {
			Logger.error("Error occured while retrieving login user from request header", e);
		}

		return null;
	}

	public static String longArrayToString(long[] dataArray) {
		return dataArray != null
				? Arrays.stream(dataArray).boxed().map(String::valueOf).collect(Collectors.joining(",")) : null;
	}

	public static long[] stringToLongArray(String data) {
		return data != null ? Arrays.stream(StringUtils.split(data, ",")).mapToLong(Long::parseLong).toArray() : null;
	}
}
