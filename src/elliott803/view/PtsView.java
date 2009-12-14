/**
 * Elliott Model 803B Simulator
 *
 * (C) Copyright Tim Baldwin 2009
 */
package elliott803.view;

import elliott803.machine.PaperTapeStation;

/**
 * The visual representation of the paper tape station.
 *
 *  Currently there is no specific view of the PTS, only a view of the reader,
 *  punch and teletype devices it contains
 *
 * @author Baldwin
 */
public class PtsView {

    ReaderView[] reader;
    PunchView[] punch;
    TeletypeView teletype;

    public PtsView(PaperTapeStation pts) {
        reader = new ReaderView[2];
        reader[0] = new ReaderView(pts.readers[PaperTapeStation.READER1], 1);
        reader[1] = new ReaderView(pts.readers[PaperTapeStation.READER2], 2);

        punch = new PunchView[2];
        punch[0] = new PunchView(pts.punches[PaperTapeStation.PUNCH1], 1);
        punch[1] = new PunchView(pts.punches[PaperTapeStation.PUNCH2], 2);
        teletype = new TeletypeView(pts.punches[PaperTapeStation.TELETYPE]);
    }
}
