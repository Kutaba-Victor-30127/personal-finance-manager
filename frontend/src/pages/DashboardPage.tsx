import { useEffect, useState } from "react";
import { getDashboard,
         getCategorySummary,
         getPeriodSummary,
 } from "../services/dashboardService";

import type { CategorySummaryResponse, PeriodSummaryResponse, GroupBy } from "../services/dashboardService";
import type { DashboardResponse } from "../types/dashboard";

import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  ToggleButton,
  ToggleButtonGroup,
  Stack,
} from "@mui/material";

import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Tooltip,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Legend,
} from "recharts";

import AccountBalanceWalletIcon from "@mui/icons-material/AccountBalanceWallet";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import TrendingDownIcon from "@mui/icons-material/TrendingDown";

type PeriodOption = "7D" | "30D" | "6W" | "3M";

const formatDate = (date: Date) => {
    const year = date.getFullYear();

    const month = String(
        date.getMonth() + 1
    ).padStart(2, "0");

    const day = String(
        date.getDate()
    ).padStart(2, "0");

    return `${year}-${month}-${day}`;
};

const getPeriodDates = (period: PeriodOption) => {

    const endDate = new Date();
    const startDate = new Date();

    switch (period) {

        case "7D":
            startDate.setDate(
                startDate.getDate() - 6
            );
            break;
        
        case "30D":
            startDate.setDate(
                startDate.getDate() - 29
            );
            break;

        case "6W":
            startDate.setDate(
                startDate.getDate() - 41
            );
            break;

        case "3M":
            startDate.setMonth(
                startDate.getMonth() - 3
            );
            break;
    }

    return {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
    };
};

const getGroupByForPeriod = (
    period: PeriodOption
): GroupBy => {

    switch (period) {

        case "7D":
        case "30D":
            return "DAY";

        case "6W":
            return "WEEK";

        case "3M":
            return "MONTH";
    }
};

const CATEGORY_COLORS = [
    "#ff6f61",
    "#ff9800",
    "#2979ff",
    "#bb5ce6",
    "#ef5bd8",
    "#00c853",
    "#00bcd4",
    "#ffc107",
];

const formatPeriodLabel = (
    dateString: string,
    period: PeriodOption
) => {

    const date = new Date(
        `${dateString}T00:00:00`
    );

    if (period === "3M") {
        return date.toLocaleDateString(
            "en-US",
            {
                month: "short",
            }
        );
    }

    return date.toLocaleDateString(
        "en-US",
        {
            day: "2-digit",
            month: "short",
        }
    );
};

export default function DashboardPage() {

    const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);

    const [categorySummary, setCategorySummary] = useState<CategorySummaryResponse[]>([]);

    const [periodSummary, setPeriodSummary] = useState<PeriodSummaryResponse[]>([]);

    const [loading, setLoading] = useState(true);

    const [period, setPeriod] = useState<PeriodOption>("30D");

    useEffect(() => {

        const loadDashboard = async () => {
            
            try {

                const filters = getPeriodDates(period);

                const groupBy = getGroupByForPeriod(period);

                const [dashboardData, categoryData, periodData] =
                            await Promise.all([
                                getDashboard(filters),
                                getCategorySummary(filters),
                                getPeriodSummary(filters, groupBy),
                            ]);

                setDashboard(dashboardData);
                setCategorySummary(categoryData);
                setPeriodSummary(periodData);    

            }catch (error) {

                console.error("Failed to load dashboard:", error);

            }finally {

                setLoading(false);
            }   
        };
        
        loadDashboard();
    
    }, [period]);

    if (loading) {
        return <h1>Loading...</h1>;
    }

    if (!dashboard) {
        return <h1>Could not load dashboard data.</h1>;
    }

    const totalCategoryExpenses = categorySummary.reduce(
        (sum, category) => sum + category.total,
        0
    );

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

        <ToggleButtonGroup
            value={period}
            exclusive
            onChange={(_, newPeriod) => {
                if (newPeriod !== null){
                    setPeriod(newPeriod);
                }
            }}
            sx={{ mb: 4}}
        >

            <ToggleButton value="7D">
                7D
            </ToggleButton>

            <ToggleButton value="30D">
                30D
            </ToggleButton>

            <ToggleButton value="6W">
                6W
            </ToggleButton>

            <ToggleButton value="3M">
                3M
            </ToggleButton>
        </ToggleButtonGroup>
        
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

        <Box sx={{ mt: 4}}>
            <Typography variant="h5" sx={{ mb: 2}}>
                Expenses by category
            </Typography>

            <Box
                sx={{
                    position: "relative",
                    width: "100%",
                    height: 320,
                }}

            >

                <ResponsiveContainer 
                    width={"100%"}
                    height={"100%"}
                >
                
                <PieChart>

                <Pie
                    data={categorySummary}
                    dataKey="total"
                    nameKey="category"
                    cx="50%"
                    cy="50%"
                    innerRadius={85}
                    outerRadius={120}
                    paddingAngle={2}
                >
                    {categorySummary.map((category, index) => (

                        <Cell
                            key={category.category}
                            fill={
                                CATEGORY_COLORS[
                                index % CATEGORY_COLORS.length
                                ]
                            }
                        />
                    ))}

                </Pie>

                <Tooltip />
                
                </PieChart>
            </ResponsiveContainer>

            <Box
                sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    textAlign: "center",
                    pointerEvents: "none",
                }}
            >
                <Typography 
                    variant="h5" 
                    sx={{ fontWeight: "bold" }}
                >
                    {totalCategoryExpenses.toFixed(2)} lei
                </Typography>

                <Typography
                    variant="body2"
                    color="text.secondary"
                >
                    Total expenses
                </Typography>
            </Box>

        </Box>

        <Stack spacing={2}>

            {categorySummary.map((category, index) => {

                const percentage = 
                    totalCategoryExpenses > 0
                    ? (category.total / totalCategoryExpenses) * 100
                    : 0;

                return(
                    <Box
                    key={category.category}
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                    }}
                >

                    <Stack
                        direction={"row"}
                        spacing={2}
                        sx={{
                            alignItems: "center",
                        }}  
                    >

                        <Box
                            sx={{
                                width: 14,
                                height: 14,
                                borderRadius: "50%",
                                backgroundColor:
                                    CATEGORY_COLORS[
                                        index % CATEGORY_COLORS.length
                                    ],
                            }}
                        />

                        <Box>
                            <Typography 
                                sx={{ fontWeight: 600 }}
                            >
                                {category.category}
                            </Typography>

                            <Typography
                                variant="body2"
                                color="text.secondary"
                            >
                                {category.transactionCount} transactions
                            </Typography>
                        </Box>

                    </Stack>

                    <Box sx={{ textAlign: "right" }}>

                        <Typography 
                            sx={{ fontWeight: 600 }}
                        >
                            {category.total.toFixed(2)} lei
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                        >
                            {percentage.toFixed(0)}%
                        </Typography>

                    </Box>

                </Box>
            );
        })}
                    
        </Stack>
    
        </Box>


        <Box sx={{ mt: 6 }}>

            <Typography variant="h5">
                Spending trend
            </Typography>
        
            <Card>
                <CardContent>

                    <Box
                        sx={{
                            width: "100%",
                            height: 350,
                        }}
                    >
                        <ResponsiveContainer
                            width="100%"
                            height="100%"
                        >

                            <AreaChart
                                data={periodSummary}
                            >

                                <CartesianGrid
                                    strokeDasharray="3 3"
                                />

                                <XAxis
                                    dataKey="periodStart"
                                    tickFormatter={(value) => 
                                        formatPeriodLabel(value, period)
                                    }    
                                />

                                <YAxis />

                                <Tooltip
                                    labelFormatter={(value) =>
                                        formatPeriodLabel(
                                            String(value),
                                            period
                                        )
                                    }
                                />

                                <Legend />

                                <Area
                                    type="monotone"
                                    dataKey="income"
                                    name="Income"
                                    stroke="#00c853"
                                    fill="#00c853"
                                    fillOpacity={0.15}
                                />

                                <Area
                                    type="monotone"
                                    dataKey="expenses"
                                    name="Expenses"
                                    stroke="#ff1744"
                                    fill="#ff1744"
                                    fillOpacity={0.15}
                                />

                            </AreaChart>
                        </ResponsiveContainer>
                    </Box>
                
                </CardContent>
            </Card>
        
        </Box>

    </Box>
    );
}
