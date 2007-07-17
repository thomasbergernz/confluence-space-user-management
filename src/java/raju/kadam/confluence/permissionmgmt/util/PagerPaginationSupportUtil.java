package raju.kadam.confluence.permissionmgmt.util;

import bucket.core.actions.PagerPaginationSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * (c) 2007 Duke University
 * User: gary.weaver@duke.edu
 * Date: Jul 16, 2007
 * Time: 11:51:30 AM
 */
public class PagerPaginationSupportUtil {

    public static final Log log = LogFactory.getLog(PagerPaginationSupportUtil.class);

    /**
     * If you get pps.getStartIndex() and remove a lot of records, this will ensure you can get back to the location
     * as close to the original index as possible to avoid disorientation.
     */
    public static void safelyMoveToOldStartIndex(int startIndex, PagerPaginationSupport pps) {
        log.debug("safelyMoveToOldStartIndex() called. startIndex=" + startIndex);
        if (pps!=null) {
            int[] startIndexes = pps.getNextStartIndexes();
            if (startIndexes!=null && startIndexes.length > 0) {
                // 0 is a valid start index not included in nextStartIndexes
                int closestStartIndex = 0;
                for (int i=0; i<startIndexes.length; i++) {
                    int nextStartIndex = startIndexes[i];
                    int oldAbsoluteDifference = Math.abs(startIndex - closestStartIndex);
                    int absoluteDifference = Math.abs(startIndex - nextStartIndex);

                    log.debug("is startIndex=" + startIndex + " closer to " + closestStartIndex + " or " + nextStartIndex + " ?");

                    if ( absoluteDifference < oldAbsoluteDifference ) {
                        log.debug("now using " + nextStartIndex + " as closest index");
                        closestStartIndex = nextStartIndex;
                    }
                }

                pps.setStartIndex(closestStartIndex);
            }
            else {
                log.debug("nextStartIndexes was null");
            }
        }
        else {
            log.debug("safelyMoveToOldStartIndex() shouldn't really be called with null. programming error");
        }
    }

    public static boolean hasNext(PagerPaginationSupport pps) {
        boolean result = false;

        log.debug("hasNext() called");
        debug(pps);
        if (pps!=null) {
            int[] startIndexes = pps.getNextStartIndexes();
            if (startIndexes!=null) {
                for (int i=(startIndexes.length-1); !result && i>=0; i--) {
                    int nextStartIndex = startIndexes[i];
                    log.debug("next start index " + nextStartIndex + "> start index " + pps.getStartIndex() + " ?");
                    if (nextStartIndex > pps.getStartIndex()) {
                        log.debug("setStartIndex to " + startIndexes[i]);
                        result = true;
                    }
                }
            }
            else {
                log.debug("nextStartIndexes was null");
            }
        }
        else {
            log.debug("next() shouldn't really be called with null. programming error");
        }

        log.debug("hasNext() returning " + result);

        return result;
    }

    public static void next( PagerPaginationSupport pps ) {
        log.debug("next() called");
        debug(pps);
        if (pps!=null) {
            int[] startIndexes = pps.getNextStartIndexes();
            if (startIndexes!=null) {
                boolean done = false;
                for (int i=0; !done && i<startIndexes.length; i++) {
                    int nextStartIndex = startIndexes[i];
                    log.debug("next start index " + nextStartIndex + "> start index " + pps.getStartIndex() + " ?");
                    if (nextStartIndex > pps.getStartIndex()) {
                        log.debug("setStartIndex to " + startIndexes[i]);
                        pps.setStartIndex(startIndexes[i]);
                        done = true;
                    }
                }
            }
            else {
                log.debug("nextStartIndexes was null");
            }
        }
        else {
            log.debug("next() shouldn't really be called with null. programming error");
        }
    }

    public static boolean hasPrev(PagerPaginationSupport pps) {
        boolean result = false;

        log.debug("hasPrev() called");
        debug(pps);
        if (pps!=null) {
            // 0 is the lowest valid start index
            if (pps.getStartIndex() > 0) {
                result = true;
            }
        }
        else {
            log.debug("prev() shouldn't really be called with null. programming error");
        }

        log.debug("hasPrev() returning " + result + ".");

        return result;
    }

    public static void prev( PagerPaginationSupport pps ) {
        log.debug("prev() called");
        debug(pps);
        if (pps!=null) {
            int[] startIndexes = pps.getNextStartIndexes();
            if (startIndexes!=null) {
                boolean done = false;
                for (int i=(startIndexes.length-1); !done && i>=0; i--) {
                    int nextStartIndex = startIndexes[i];
                    log.debug("previous start index " + nextStartIndex + "< start index " + pps.getStartIndex() + " ?");
                    if (nextStartIndex < pps.getStartIndex()) {
                        log.debug("setStartIndex to " + startIndexes[i]);
                        pps.setStartIndex(startIndexes[i]);
                        done=true;
                    }
                }

                // 0 is the first valid start index (not included in nextStartIndexes)
                if (!done && pps.getStartIndex() > 0) {
                    pps.setStartIndex(0);
                }
            }
            else {
                log.debug("previousStartIndexes was null");
            }
        }
        else {
            log.debug("prev() shouldn't really be called with null. programming error");
        }
    }

    public static int getPageEndIndex( PagerPaginationSupport pps ) {
        int i = pps.getStartIndex() + pps.getCountOnEachPage();
        if ( pps.getTotal() < i ) {
            i = pps.getTotal();
        }
        return i;
    }

    private static void debug( PagerPaginationSupport pps ) {
        StringBuffer sb = new StringBuffer();
        sb.append("PagerPaginationSupport debug:");
        if (pps!=null) {
            try {
                sb.append("\nCountOnEachPage=" + pps.getCountOnEachPage());
                sb.append("\nEndIndex=" + pps.getEndIndex());
                //doesn't do null check?
                //sb.append("\nNextIndex=" + pps.getNextIndex());
                sb.append("\nNextStartIndexes=" + intArrayToString(pps.getNextStartIndexes()));
                sb.append("\nNiceStartIndex=" + pps.getNiceStartIndex());
                //doesn't do null check
                //sb.append("\nPreviousIndex=" + pps.getPreviousIndex());
                sb.append("\nPreviousStartIndexes=" + intArrayToString(pps.getPreviousStartIndexes()));
                sb.append("\nStartIndex=" + pps.getStartIndex());
                sb.append("\nStartIndexValue=" + pps.getStartIndexValue());
                sb.append("\nTotal=" + pps.getTotal());
            }
            catch (Throwable t) {
                sb.append("failed at this point");
                log.error("Failed to debug PPS", t);
            }
        }
        else {
            sb.append(" PPS was null");
        }
        log.debug(sb.toString());
    }

    private static String intArrayToString(int[] ints) {
        StringBuffer sb = new StringBuffer();
        if (ints!=null) {
            for (int i=0; i<ints.length; i++) {
                if (i!=0) {
                    sb.append(", ");
                }
                sb.append( ints[i] );
            }
        }
        else {
            sb.append("null");
        }
        return sb.toString();
    }
}