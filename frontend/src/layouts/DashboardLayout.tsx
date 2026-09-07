import {
  AppBar,
  Box,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Button,
} from "@mui/material";

import DashboardIcon from "@mui/icons-material/Dashboard";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import CategoryIcon from "@mui/icons-material/Category";
import LogoutIcon from "@mui/icons-material/Logout";

import { Link, Outlet, useNavigate } from "react-router-dom";

import { logout } from "../services/authService";
import {
  clearTokens,
  getRefreshToken,
} from "../utils/tokenStorage";


const drawerWidth = 240;


export default function DashboardLayout() {

  const navigate = useNavigate();


  const handleLogout = async () => {

    const refreshToken = getRefreshToken();

    try {

      if (refreshToken) {
        await logout(refreshToken);
      }

    } catch (error) {

      console.error("Logout failed:", error);

    } finally {

      clearTokens();

      navigate("/login");
    }
  };


  return (
    <Box sx={{ display: "flex" }}>

      <AppBar
        position="fixed"
        sx={{
          width: `calc(100% - ${drawerWidth}px)`,
          ml: `${drawerWidth}px`,
        }}
      >
        <Toolbar>

          <Typography
            variant="h6"
            sx={{ flexGrow: 1 }}
          >
            Personal Finance Manager
          </Typography>

          <Button
            color="inherit"
            startIcon={<LogoutIcon />}
            onClick={handleLogout}
          >
            Logout
          </Button>

        </Toolbar>
      </AppBar>


      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,

          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
          },
        }}
      >

        <Toolbar>

          <Typography variant="h6">
            Finance
          </Typography>

        </Toolbar>


        <List>

          <ListItemButton
            component={Link}
            to="/dashboard"
          >
            <ListItemIcon>
              <DashboardIcon />
            </ListItemIcon>

            <ListItemText primary="Dashboard" />
          </ListItemButton>


          <ListItemButton
            component={Link}
            to="/transactions"
          >
            <ListItemIcon>
              <ReceiptLongIcon />
            </ListItemIcon>

            <ListItemText primary="Transactions" />
          </ListItemButton>


          <ListItemButton
            component={Link}
            to="/categories"
          >
            <ListItemIcon>
              <CategoryIcon />
            </ListItemIcon>

            <ListItemText primary="Categories" />
          </ListItemButton>

        </List>

      </Drawer>


      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          ml: `${drawerWidth}px`,
        }}
      >

        <Toolbar />

        <Outlet />

      </Box>

    </Box>
  );
}