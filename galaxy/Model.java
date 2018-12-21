package galaxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static galaxy.Place.pl;


/**
 * The state of a Galaxies Puzzle.  Each cell, cell edge, and intersection of
 * edges has coordinates (x, y). For cells, x and y are positive and odd.
 * For intersections, x and y are even.  For horizontal edges, x is odd and
 * y is even.  For vertical edges, x is even and y is odd.  On a board
 * with w columns and h rowsnum of cells, (0, 0) indicates the bottom left
 * corner of the board, and (2w, 2h) indicates the upper right corner.
 * If (x, y) are the coordinates of a cell, then (x-1, y) is its left edge,
 * (x+1, y) its right edge, (x, y-1) its bottom edge, and (x, y+1) its
 * top edge.  The four cells (x, y), (x+2, y), (x, y+2), and (x+2, y+2)
 * meet at intersection (x+1, y+1).  Cells contain nonnegative integer
 * values, or "marks". A cell containing 0 is said to be unmarked.
 *
 * @author Karina Patel
 */

class Model {

    /**
     * The number of columns for the board.
     */
    private int columns;
    /**
     * The default number of rowsnum for the board.
     */
    private int rowsnum;
    /**
     * The game board.
     */
    private Place[][] board;
    /**
     * centers, boundaries.
     */
    private ArrayList<Place> centers, boundaries;
    /**
     * Keeps track of markings.
     */
    private int[][] marks;
    /**
     * The default number of squares on a side of the board.
     */
    static final int DEFAULT_SIZE = 7;

    /**
     * Initializes an empty puzzle board of size DEFAULT_SIZE x DEFAULT_SIZE,
     * with a boundary around the periphery.
     */
    Model() {
        init(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * Initializes an empty puzzle board of size COLS x ROWS, with a boundary
     * around the periphery.
     */
    Model(int cols, int rows) {
        init(cols, rows);
    }

    /**
     * Initializes a copy of MODEL.
     */
    Model(Model model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Model model) {
        if (model != this) {
            this.columns = model.columns;
            this.rowsnum = model.rowsnum;
            this.board = new Place[xlim()][ylim()];
            this.marks = new int[xlim()][ylim()];
            this.centers = new ArrayList<Place>(model.centers);
            this.boundaries = new ArrayList<Place>(model.boundaries);
            int i = 0;
            while (i < xlim()) {
                System.arraycopy(model.board[i], 0, board[i],
                        0, board[i].length);
                System.arraycopy(model.marks[i], 0, marks[i],
                        0, marks[i].length);
                i++;
            }
        } else {
            return;
        }
    }

    /**
     * Sets the puzzle board size to COLS x ROWS, and clears it.
     */
    void init(int cols, int rows) {
        this.columns = cols;
        this.rowsnum = rows;
        int xDim = xlim();
        int yDim = ylim();
        this.board = new Place[xDim][yDim];
        this.centers = new ArrayList<Place>();
        this.boundaries = new ArrayList<Place>();
        this.marks = new int[xDim][yDim];
        int i = 0;
        while (i < xDim) {
            for (int j = 0; j < yDim; j++) {
                if (isCell(i, j)) {
                    this.marks[i][j] = 0;
                }
            }
            i++;
        }
    }

    /**
     * Clears the board (removes centers, boundaries that are not on the
     * periphery, and marked cells) without resizing.
     */
    void clear() {
        init(cols(), rows());
    }

    /**
     * Returns the number of columns of cells in the board.
     */
    int cols() {
        return xlim() / 2;
    }

    /**
     * Returns the number of rowsnum of cells in the board.
     */
    int rows() {
        return ylim() / 2;
    }

    /**
     * Returns the number of vertical edges and cells in a row.
     */
    int xlim() {
        return columns * 2 + 1;
    }

    /**
     * Returns the number of horizontal edges and cells in a column.
     */
    int ylim() {

        return rowsnum * 2 + 1;
    }

    /**
     * Returns true iff (X, Y) is a valid cell.
     */
    boolean isCell(int x, int y) {
        if ((0 <= y) && (y < ylim())) {
            if ((0 <= x) && (x < xlim())) {
                if ((x % 2 == 1) && (y % 2 == 1)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Returns true iff P is a valid cell.
     */
    boolean isCell(Place p) {
        return (isCell(p.x, p.y));
    }

    /**
     * Returns true iff (X, Y) is a valid edge.
     */
    boolean isEdge(int x, int y) {
        if ((0 <= y) && (y < ylim())) {
            if ((0 <= x) && (x < xlim())) {
                if (x % 2 != y % 2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true iff P is a valid edge.
     */
    boolean isEdge(Place p) {
        return (isEdge(p.x, p.y));
    }

    /**
     * Returns true iff (X, Y) is a vertical edge.
     */
    boolean isVert(int x, int y) {
        if (isEdge(x, y)) {
            if (x % 2 == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true iff P is a vertical edge.
     */
    boolean isVert(Place p) {
        return (isVert(p.x, p.y));
    }

    /**
     * Returns true iff (X, Y) is a horizontal edge.
     */
    boolean isHoriz(int x, int y) {
        if (isEdge(x, y)) {
            if (y % 2 == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true iff P is a horizontal edge.
     */
    boolean isHoriz(Place p) {
        return (isHoriz(p.x, p.y));
    }

    /**
     * Returns true iff (X, Y) is a valid intersection.
     */
    boolean isIntersection(int x, int y) {
        if ((x >= 0) && (y >= 0)) {
            if ((x % 2 == 0) && (y % 2 == 0)) {
                if ((x < xlim()) && (y < ylim())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true iff P is a valid intersection.
     */
    boolean isIntersection(Place p) {
        return (isIntersection(p.x, p.y));
    }

    /**
     * Returns true iff (X, Y) is a centers.
     */
    boolean isCenter(int x, int y) {
        return isCenter(pl(x, y));
    }

    /**
     * Returns true iff P is a centers.
     */
    boolean isCenter(Place p) {
        return (centers().contains(p));
    }

    /**
     * Returns true iff (X, Y) is a boundary.
     */
    boolean isBoundary(int x, int y) {
        if (x == 0 || x == xlim() - 1) {
            return y % 2 != 0;
        } else if (y == 0 || y == ylim() - 1) {
            return x % 2 != 0;
        } else {
            return boundaries.contains(pl(x, y));
        }
    }

    /**
     * Returns true iff P is a boundary.
     */
    boolean isBoundary(Place p) {
        return (isBoundary(p.x, p.y));
    }

    /**
     * Returns true iff the puzzle board is solved, given the centers and
     * boundaries that are currently on the board.
     */
    boolean solved() {
        int total;
        total = 0;
        for (Place c : centers()) {
            HashSet<Place> r = findGalaxy(c);
            if (r == null) {
                return false;
            } else {
                total += r.size();
            }
        }
        return total == rows() * cols();
    }

    /**
     * Finds cells reachable from CELL and adds them to REGION.  Specifically,
     * it finds cells that are reachable using only vertical and horizontal
     * moves starting from CELL that do not cross any boundaries and
     * do not touch any cells that were initially in REGION. Requires
     * that CELL is a valid cell.
     */
    private void accreteRegion(Place cell, HashSet<Place> region) {
        assert isCell(cell);
        if (region.contains(cell)) {
            return;
        }
        region.add(cell);
        int i = 0;
        while (i < 4) {
            int dx = (i % 2) * (2 * (i / 2) - 1),
                    dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
            if (isBoundary(cell.move(dx, dy))) {
                i += 1;
                continue;
            } else {
                accreteRegion(cell.move(2 * dx, 2 * dy), region);
                i += 1;
            }
        }
    }

    /**
     * Returns true iff REGION is a correctly formed galaxy. A correctly formed
     * galaxy has the following characteristics:
     * - is symmetric about CENTER,
     * - contains no interior boundaries, and
     * - contains no other centers.
     * Assumes that REGION is connected.
     */
    private boolean isGalaxy(Place center, HashSet<Place> region) {
        for (Place cell : region) {
            if (!region.contains(opposing(center, cell))
                    || (isCenter(cell) && (center != cell))) {
                return false;
            }
            for (int i = 0; i < 4; i += 1) {
                int dx = (i % 2) * (2 * (i / 2) - 1),
                        dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
                Place boundary = cell.move(dx, dy),
                        nextCell = cell.move(2 * dx, 2 * dy);

                if ((center != boundary && isCenter(boundary))
                        || (region.contains(nextCell)
                        && isBoundary(boundary))) {
                    return false;
                }
            }
            int i = 0;
            while (i < 4) {
                int dx = 2 * (i / 2) - 1,
                        dy = 2 * (i % 2) - 1;
                Place intersection = cell.move(dx, dy);
                if (!isCenter(intersection) || (center == intersection)) {
                    i += 1;
                } else {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Returns the galaxy containing CENTER that has the following
     * characteristics:
     * - encloses CENTER completely,
     * - is symmetric about CENTER,
     * - is connected,
     * - contains no stray boundary edges, and
     * - contains no other centers aside from CENTER.
     * Otherwise, returns null. Requires that CENTER is not on the
     * periphery.
     */
    HashSet<Place> findGalaxy(Place center) {
        HashSet<Place> galaxy = new HashSet<>();
        if (isIntersection(center)) {
            for (int i = 0; i < 4; i += 1) {
                int dx = 2 * (i / 2) - 1, dy = 2 * (i % 2) - 1;
                accreteRegion(center.move(dx, dy), galaxy);
            }
        } else if (isHoriz(center)) {
            accreteRegion(center.move(0, 1), galaxy);
            accreteRegion(center.move(0, -1), galaxy);
        } else if (isVert(center)) {
            accreteRegion(center.move(-1, 0), galaxy);
            accreteRegion(center.move(1, 0), galaxy);
        } else if (isCell(center)) {
            accreteRegion(center, galaxy);
        }
        if (isGalaxy(center, galaxy)) {
            return galaxy;
        } else {
            return null;
        }

    }

    /**
     * Returns the largest, unmarked region around CENTER with the
     * following characteristics:
     * - contains all cells touching CENTER,
     * - consists only of unmarked cells,
     * - is symmetric about CENTER, and
     * - is contiguous.
     * The method ignores boundaries and other centers on the current board.
     * If there is no such region, returns the empty set.
     */
    Set<Place> maxUnmarkedRegion(Place center) {
        HashSet<Place> region = new HashSet<>();
        region.addAll(unmarkedContaining(center));
        List<Place> regionlist = new ArrayList<Place>(region);
        while (!regionlist.isEmpty()) {
            region.addAll(unmarkedSymAdjacent(center, regionlist));
            regionlist = unmarkedSymAdjacent(center, regionlist);
            markAll(region, 1);
        }
        markAll(region, 0);
        return region;
    }

    /**
     * Marks all properly formed galaxies with value V. Unmarks all cells that
     * are not contained in any of these galaxies. Requires that V is greater
     * than or equal to 0.
     */
    void markGalaxies(int v) {
        assert v >= 0;
        markAll(0);
        for (Place c : centers()) {
            HashSet<Place> region = findGalaxy(c);
            if (region != null) {
                markAll(region, v);
            }
        }
    }

    /**
     * Toggles the presence of a boundary at the edge (X, Y). That is, negates
     * the value of isBoundary(X, Y) (from true to false or vice-versa).
     * Requires that (X, Y) is an edge.
     */
    void toggleBoundary(int x, int y) {
        if (isEdge(x, y)) {
            if (boundaries.contains(pl(x, y))) {
                boundaries.remove(pl(x, y));
            } else {
                boundaries.add(pl(x, y));
            }
        }
    }


    /**
     * Places a centers at (X, Y). Requires that X and Y are within bounds of
     * the board.
     */
    void placeCenter(int x, int y) {
        placeCenter(pl(x, y));
    }

    /**
     * Places centers at P.
     */
    void placeCenter(Place p) {
        centers.add(p);
    }


    /**
     * Returns the current mark on cell (X, Y), or -1 if (X, Y) is not a valid
     * cell address.
     */
    int mark(int x, int y) {
        if (isCell(x, y)) {
            return marks[x][y];
        }
        return -1;
    }

    /**
     * Returns the current mark on cell P, or -1 if P is not a valid cell
     * address.
     */
    int mark(Place p) {
        return mark(p.x, p.y);
    }


    /**
     * Marks the cell at (X, Y) with value V. Requires that V must be greater
     * than or equal to 0, and that (X, Y) is a valid cell address.
     */
    void mark(int x, int y, int v) {
        if (isCell(x, y)) {
            if (v < 0) {
                throw new IllegalArgumentException("bad mark value");
            } else {
                marks[x][y] = v;
            }
        } else {
            throw new IllegalArgumentException("bad cell coordinates");
        }
    }

    /**
     * Marks the cell at P with value V. Requires that V must be greater
     * than or equal to 0, and that P is a valid cell address.
     */
    void mark(Place p, int v) {
        mark(p.x, p.y, v);
    }

    /**
     * Sets the marks of all cells in CELLS to V. Requires that V must be
     * greater than or equal to 0.
     */
    void markAll(Collection<Place> cells, int v) {
        assert v >= 0;
        for (Place p : cells) {
            mark(p, v);
        }
    }

    /**
     * Sets the marks of all cells to V. Requires that V must be greater than
     * or equal to 0.
     */
    void markAll(int v) {
        assert v >= 0;
        for (int i = 0; i < xlim(); i++) {
            for (int j = 0; j < ylim(); j++) {
                if (isCell(i, j)) {
                    mark(i, j, v);
                }
            }
        }
    }

    /**
     * Returns the position of the cell that is opposite P using P0 as the
     * centers, or null if that is not a valid cell address.
     */
    Place opposing(Place p0, Place p) {
        int xdif = p0.x - p.x;
        int ydif = p0.y - p.y;
        int newX = p0.x + xdif;
        int newY = p0.y + ydif;
        if (!isCell(newX, newY)) {
            return null;
        }
        return pl(newX, newY);
    }

    /**
     * Returns a list of all cells "containing" PLACE if all of the cells are
     * unmarked. A cell, c, "contains" PLACE if
     * - c is PLACE itself,
     * - PLACE is a corner of c, or
     * - PLACE is an edge of c.
     * Otherwise, returns an empty list.
     */
    List<Place> unmarkedContaining(Place place) {
        if (isCell(place)) {
            if (mark(place) != 0) {
                return Collections.emptyList();
            }
            return asList(place);
        } else if (isVert(place)) {
            if (mark(pl(place.x + 1, place.y)) != 0
                    || mark(pl(place.x - 1, place.y)) != 0) {
                return Collections.emptyList();
            }
            return asList(place.move(-1, 0), place.move(1, 0));
        } else if (isHoriz(place)) {

            if (mark(pl(place.x, place.y + 1)) != 0
                    || mark(pl(place.x, place.y - 1)) != 0) {
                return Collections.emptyList();
            }
            return asList(place.move(0, -1), place.move(0, 1));
        } else {
            int i = 0;
            while (i < 4) {
                if (mark(place.move(i % 2 + i % 2 - 1,
                        i / 2 + i / 2 - 1)) == 0) {
                    i += 1;
                } else {
                    return Collections.emptyList();
                }
            }
            return asList(place.move(-1, -1), place.move(1, 1),
                    place.move(1, -1), place.move(-1, 1));
        }
    }

    /**
     * Returns a list of all cells, c, such that:
     * - c is unmarked,
     * - The opposite cell from c relative to CENTER exists and
     * is unmarked, and
     * - c is vertically or horizontally adjacent to a cell in REGION.
     * CENTER and all cells in REGION must be valid cell positions.
     * Each cell appears at most once in the resulting list.
     */
    List<Place> unmarkedSymAdjacent(Place center, List<Place> region) {
        ArrayList<Place> result = new ArrayList<>();
        for (Place r : region) {
            assert isCell(r);
            int i = 0;
            while (i < 4) {
                Place p = r.move(2 * (i % 2) * (2 * (i / 2) - 1),
                        2 * ((i + 1) % 2) * (2 * (i / 2) - 1));
                Place opp = opposing(center, p);
                if (opp != null) {
                    if (mark(opp) != 0 || mark(p) != 0) {
                        int elem = 0;
                    } else {
                        result.add(p);
                    }
                }
                i += 1;
            }

        }
        return result;
    }

    /**
     * Returns an unmodifiable view of the list of all centers.
     */
    List<Place> centers() {
        return Collections.unmodifiableList(centers);
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        int w = xlim(), h = ylim();
        for (int y = h - 1; y >= 0; y -= 1) {
            for (int x = 0; x < w; x += 1) {
                boolean cent = isCenter(x, y);
                boolean bound = isBoundary(x, y);
                if (isIntersection(x, y)) {
                    out.format(cent ? "o" : " ");
                } else if (isCell(x, y)) {
                    if (cent) {
                        out.format(mark(x, y) > 0 ? "O" : "o");
                    } else {
                        out.format(mark(x, y) > 0 ? "*" : " ");
                    }
                } else if (y % 2 == 0) {
                    if (cent) {
                        out.format(bound ? "O" : "o");
                    } else {
                        out.format(bound ? "=" : "-");
                    }
                } else if (cent) {
                    out.format(bound ? "O" : "o");
                } else {
                    out.format(bound ? "I" : "|");
                }
            }
            out.format("%n");
        }
        return out.toString();
    }


}
