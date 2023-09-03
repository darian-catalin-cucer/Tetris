package cucerdariancatalin.tetris.figures

// Class representing the "I" Tetris figure
class IFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"0100"
        +"0100"
        +"0100"
        +"0100"
    }
}

// Class representing the "L" Tetris figure
class LFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"100"
        +"100"
        +"110"
    }
}

// Class representing the flipped "L" Tetris figure
class LFlippedFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"001"
        +"001"
        +"011"
    }
}

// Class representing the "S" Tetris figure
class SFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"011"
        +"110"
        +"000"
    }
}

// Class representing the flipped "S" Tetris figure
class SFlippedFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"110"
        +"011"
        +"000"
    }
}

// Class representing the square Tetris figure
class SquareFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"11"
        +"11"
    }
}

// Class representing the "T" Tetris figure
class TFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"010"
        +"111"
        +"000"
    }
}
