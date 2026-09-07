import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    mode: "dark",

    primary: {
      main: "#7c5cff",
    },

    secondary: {
      main: "#22c55e",
    },

    background: {
      default: "#0f1117",
      paper: "#171a23",
    },
  },

  typography: {
    fontFamily: "Inter, Arial, sans-serif",

    h4: {
      fontWeight: 700,
    },

    h6: {
      fontWeight: 600,
    },
  },

  shape: {
    borderRadius: 12,
  },
});

export default theme;