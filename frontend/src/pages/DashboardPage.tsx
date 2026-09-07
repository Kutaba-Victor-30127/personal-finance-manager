import { useEffect, useState } from "react";
import { getDashboard } from "../services/dashboardService";
import type { DashboardResponse } from "../types/dashboard";

import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Stack,
} from "@mui/material";

import AccountBalanceWalletIcon from "@mui/icons-material/AccountBalanceWallet";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import TrendingDownIcon from "@mui/icons-material/TrendingDown";

export default function DashboardPage() {

    const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);

    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const loadDashboard = async () => {
            
            try {

                const data = await getDashboard();

                setDashboard(data);
            }catch (error) {

                console.error("Failed to load dashboard:", error);

            }finally {

                setLoading(false);
            }   
        };
        
        loadDashboard();
    
    }, []);

    if (loading) {
        return <h1>Loading...</h1>;
    }

    if (!dashboard) {
        return <h1>Could not load dashboard data.</h1>;
    }

    return (
    <Box>

        <Typography
        variant="h4"
        sx={{ mb: 1 }}
        >
        Dashboard
        </Typography>

        <Typography
        color="text.secondary"
        sx={{ mb: 4 }}
        >
        Overview of your finances
        </Typography>


        <Grid container spacing={3}>

        <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: "100%" }}>
            <CardContent>

                <Stack
                direction="row"
                sx={{
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
                >

                <Box>
                    <Typography color="text.secondary">
                    Balance
                    </Typography>

                    <Typography
                    variant="h4"
                    sx={{ mt: 1 }}
                    >
                    {dashboard.balance} lei
                    </Typography>
                </Box>

                <AccountBalanceWalletIcon
                    sx={{ fontSize: 40 }}
                    color="primary"
                />

                </Stack>

            </CardContent>
            </Card>
        </Grid>


        <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: "100%" }}>
            <CardContent>

                <Stack
                direction="row"
                sx={{
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
                >

                <Box>
                    <Typography color="text.secondary">
                    Total Income
                    </Typography>

                    <Typography
                    variant="h4"
                    sx={{ mt: 1 }}
                    >
                    {dashboard.totalIncome} lei
                    </Typography>
                </Box>

                <TrendingUpIcon
                    sx={{ fontSize: 40 }}
                    color="success"
                />

                </Stack>

            </CardContent>
            </Card>
        </Grid>


        <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: "100%" }}>
            <CardContent>

                <Stack
                direction="row"
                sx={{
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
                >

                <Box>
                    <Typography color="text.secondary">
                    Total Expenses
                    </Typography>

                    <Typography
                    variant="h4"
                    sx={{ mt: 1 }}
                    >
                    {dashboard.totalExpenses} lei
                    </Typography>
                </Box>

                <TrendingDownIcon
                    sx={{ fontSize: 40 }}
                    color="error"
                />

                </Stack>

            </CardContent>
            </Card>
        </Grid>

        </Grid>

    </Box>
    );
}
