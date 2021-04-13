package ch.bubendorf.locusaddon.gsakdatabase.receiver;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import ch.bubendorf.locusaddon.gsakdatabase.util.GeocacheAsyncTask;
import ch.bubendorf.locusaddon.gsakdatabase.util.GsakReader;
import ch.bubendorf.locusaddon.gsakdatabase.util.CacheWrapper;
import locus.api.android.ActionDisplayPoints;
import locus.api.objects.extra.Location;

public class PointLoaderAsyncTask extends GeocacheAsyncTask {

    private final Context context;

    public PointLoaderAsyncTask(final Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        openDatabases(context);
    }

    @Override
    protected Exception doInBackground(final Location... locations) {
        try {
            if (isCancelled()) {
                return null;
            }

            final List<CacheWrapper> gcCodes = GsakReader.readGCCodes(context, this,
                    db, db2, db3, locations[0], locations[1], locations[2]);
            packPoints = GsakReader.readGeocaches(this, gcCodes);
            return null;
        } catch (final Exception e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(final Exception exception) {
        super.onPostExecute(exception);
        closeDatabases();

        if (exception != null) {
//                Log.w(TAG, exception);
            Toast.makeText(context, "Error: " + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (packPoints != null && packPoints.getPoints().length > 0) {
            try {
                ActionDisplayPoints.INSTANCE.sendPackSilent(context, packPoints, false);
            } catch (final Exception e) {
                Toast.makeText(context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}