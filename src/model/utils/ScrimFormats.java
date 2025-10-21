package model.utils;

public class ScrimFormats implements ScrimFormat {
    @Override
    public int getPlayersPerTeam() {
        return 1;
    }

    @Override
    public String getFormatName() {
        return "1v1";
    }

    @Override
    public boolean isValidFormat() {
        return true;
    }
}

class Format3v3 implements ScrimFormat {
    @Override
    public int getPlayersPerTeam() {
        return 3;
    }

    @Override
    public String getFormatName() {
        return "3v3";
    }

    @Override
    public boolean isValidFormat() {
        return true;
    }
}

class Format5v5 implements ScrimFormat {
    @Override
    public int getPlayersPerTeam() {
        return 5;
    }

    @Override
    public String getFormatName() {
        return "5v5";
    }

    @Override
    public boolean isValidFormat() {
        return true;
    }
}

class InvalidFormat implements ScrimFormat {
    @Override
    public int getPlayersPerTeam() {
        return 0;
    }

    @Override
    public String getFormatName() {
        return "Invalid";
    }

    @Override
    public boolean isValidFormat() {
        return false;
    }
}