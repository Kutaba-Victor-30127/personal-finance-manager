import { useCallback, useEffect, useState  } from "react";

import{Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableSortLabel,
  TableRow,
  Typography,
  TextField,
  MenuItem,
  IconButton,
  Tooltip,
  Snackbar,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import DeleteIcon from "@mui/icons-material/Delete";

import EditIcon from "@mui/icons-material/Edit";

import AddTransactionDialog from "../components/AddTransactionDialog";

import { getTransactions, deleteTransaction} from "../services/transactionService";

import type { Transaction } from "../types/transaction";

import type { Category } from "../types/category";

import { getCategories } from "../services/categoryService";

type SortField = "date" | "title" | "amount";

type SortDirection = "asc" | "desc";

export default function TransactionsPage() {

  const [dialogOpen, setDialogOpen] = useState(false);

  const [transactions, setTransactions] = useState<Transaction[]>([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState<string | null>(null);

  const [selectedTransaction, setSelectedTransaction] = useState<Transaction | null>(null);
  
  const [page, setPage] = useState(0);

  const [size , setSize] = useState(10);

  const [totalElements, setTotalElements] = useState(0);

  const [sortBy, setSortBy] = useState<SortField>("date");

  const [sortDir, setSortDir] = useState<SortDirection>("desc");

  const [categories, setCategories] =
  useState<Category[]>([]);

  const [typeFilter, setTypeFilter] =
    useState<"" | "INCOME" | "EXPENSE">("");

  const [categoryFilter, setCategoryFilter] =
    useState<number | "">("");

  const [startDate, setStartDate] =
    useState("");

  const [endDate, setEndDate] =
    useState("");

  const [debounceStartDate, setDebounceStartDate] = useState("");

  const [debounceEndDate, setDebounceEndDate] = useState("");

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success" as "success" | "error",
  });

  const loadTransactions = useCallback(async () => {

      try{
        
        setError(null);

        const response = await getTransactions(
          page,
          size,
          sortBy, 
          sortDir,
          { type: 
                typeFilter=== "" ? undefined : typeFilter,

            categoryId:
                categoryFilter === "" ? undefined : categoryFilter,

            startDate:
                debounceStartDate === "" ? undefined : debounceStartDate,

            endDate:
                debounceEndDate === "" ? undefined : debounceEndDate,
          }
        );

        setTransactions(response.content);

        setTotalElements(response.totalElements);

      }catch (error) {

        console.error(
          "Failed to load transactions.",
          error
        );

        setError(
          "Could not load transactions.",
        );

      }finally {
      
        setLoading(false);
      }
    
    }, [page, size, sortBy, sortDir, typeFilter, categoryFilter, debounceStartDate, debounceEndDate]);

  const handleDeleteTransaction = async (
    id: number
  ) => {

    const confirmed = window.confirm(
      "Are you sure you want to delete this transaction?"
    );

    if (!confirmed) {
      return;
    }
  
    try {

      await deleteTransaction(id);

      await loadTransactions();

      setSnackbar({
        open: true,
        message: "Transaction deleted successfully.",
        severity: "success",
      });

    }catch (error){

      console.error(
        "Failed to delete transaction.",
        error
      );  

      setSnackbar({
        open: true,
        message: "Failed to delete transaction.",
        severity: "error",
      });
    }
  };

    useEffect(() => {
      void loadTransactions();
    }, [loadTransactions]);

    useEffect(() => {

      const loadCategories = async () => {

        try {

          const data = await getCategories();

          setCategories(data);

        }catch (error) {

          console.error(
            "Failed to load categories:",
            error
          );
        }
      };

      void loadCategories();

    }, []);

    useEffect(() => {

      const timeoutId = setTimeout(() => {

        setDebounceStartDate(startDate);
        setDebounceEndDate(endDate);

    }, 500);

    return () => {
      clearTimeout(timeoutId);
    };

  }, [startDate, endDate]);


  if (loading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          mt: 5,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }


  if (error) {
    return (
      <Alert severity="error">
        {error}
      </Alert>
    );
  }

  const handleChangePage = (
    _event: unknown,
    newPage: number
  ) => {

    setPage(newPage);
  };

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {

    const newSize = parseInt(event.target.value, 10);
    
    setSize(newSize);

    setPage(0);
  };

  const handleSort = (
    field: SortField
  ) => {

    if (sortBy === field) {

      setSortDir(
        sortDir === "asc" ? "desc" : "asc"
      );

    } else {

      setSortBy(field);

      setSortDir("asc");
    }

    setPage(0);
  };

  return (
  <Box>

    <Box
      sx={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        mb: 4,
      }}
    >

      <Box>

        <Typography variant="h4">
          Transactions
        </Typography>

        <Typography color="text.secondary">
          Manage your income and expenses
        </Typography>

      </Box>


      <Button
        variant="contained"
        startIcon={<AddIcon />}
        onClick={() => {
            setSelectedTransaction(null);
            setDialogOpen(true);
          }
        }
      >
        Add Transaction
      </Button>

    </Box>

    <Box
      sx={{
        display: "flex",
        gap: 2,
        mb: 3,
        flexWrap: "wrap",
      }}
    >

      <TextField
        select
        label="Type"
        value={typeFilter}
        sx={{ minWidth: 160 }}

        onChange={(event) => {

          setTypeFilter(
            event.target.value as "" | "INCOME" | "EXPENSE"
          );

          setPage(0);
        }}
        >
          
          <MenuItem value="">
            All types
          </MenuItem>

          <MenuItem value="INCOME">
            Income
          </MenuItem>

          <MenuItem value="EXPENSE">
            Expense
          </MenuItem>

        </TextField>

      <TextField
        select
        label="Category"
        value={categoryFilter}
        sx={{ minWidth: 180 }}

        onChange={(event) => {

          const value = event.target.value;

          setCategoryFilter(
            value === "" ? "" : Number(value)
          );

          setPage(0);
        }}
        >

          <MenuItem value="">
            All categories
          </MenuItem>

          {categories.map(
            (category) => (

              <MenuItem
                key={category.id}
                value={category.id}
              >
                {category.name}
              </MenuItem>
            ))}

        </TextField>

      <TextField
        label="From"
        type="date"
        value={startDate}
        
        onChange={(event) => {

          setStartDate(event.target.value);

          setPage(0);
        }}

        slotProps={{
          inputLabel: {
            shrink: true,
          },
        }}
      /> 

      <TextField
        label="To"
        type="date"
        value={endDate}

        onChange={(event) => {

          setEndDate(event.target.value); 

          setPage(0);
        }}

        slotProps={{
          inputLabel: {
            shrink: true,
          },
        }}
      />  

      <Button
        variant="outlined"

        onClick={() => {

          setTypeFilter("");

          setCategoryFilter("");

          setStartDate("");

          setEndDate("");

          setPage(0);
        }}
      >
        Clear Filters
      </Button>

    </Box>

    <TableContainer component={Paper}>

      <Table>

        <TableHead>

          <TableRow>

            <TableCell>
              
              <TableSortLabel
                active={sortBy === "date"}
                direction={sortBy === "date" ? sortDir : "asc"}
                onClick={() => handleSort("date")}
              >
                Date
              </TableSortLabel>

            </TableCell>

            <TableCell>

              <TableSortLabel
                active={sortBy === "title"}
                direction={sortBy === "title" ? sortDir : "asc"}
                onClick={() => handleSort("title")}
              >
                Title
              </TableSortLabel>

            </TableCell>

            <TableCell>
              Category
            </TableCell>

            <TableCell>
              Type
            </TableCell>

            <TableCell align="right">

              <TableSortLabel
                active={sortBy === "amount"}
                direction={sortBy === "amount" ? sortDir : "asc"} 
                onClick={() => handleSort("amount")}
              >
                Amount
              </TableSortLabel>

            </TableCell>

            <TableCell align="center">
              Actions
            </TableCell>

          </TableRow>

        </TableHead>


        <TableBody>

          {transactions.length === 0 ? (
            
            <TableRow>

              <TableCell
                colSpan={6}
                align="center"
                sx={{ py: 6 }}
              >

              <Typography 
                variant="h6"
                sx={{mb: 1}}
              >
                No transactions found.
              </Typography>

              <Typography
                color="text.secondary"

              >
                Try changing your filters.
              </Typography>

            </TableCell>

            </TableRow>
          ) :

      (transactions.map((transaction) => (

            <TableRow
              key={transaction.id}
            >

              <TableCell>
                {transaction.date}
              </TableCell>


              <TableCell>

                <Typography
                  sx={{ fontWeight: 600 }}
                >
                  {transaction.title}
                </Typography>

                <Typography
                  variant="body2"
                  color="text.secondary"
                >
                  {transaction.description}
                </Typography>

              </TableCell>


              <TableCell>
                {transaction.categoryName}
              </TableCell>


              <TableCell>

                <Chip
                  label={transaction.type}
                  color={
                    transaction.type === "INCOME"
                      ? "success"
                      : "error"
                  }
                  size="small"
                />

              </TableCell>


              <TableCell align="right">

                <Typography
                  sx={{
                    fontWeight: 600,
                    color:
                      transaction.type === "INCOME"
                        ? "success.main"
                        : "error.main",
                  }}
                >

                  {transaction.type === "INCOME"
                    ? "+"
                    : "-"
                  }

                  {transaction.amount.toFixed(2)} lei

                </Typography>

              </TableCell>

              <TableCell align="center">
                
                <Tooltip title="Edit transaction">

                  <IconButton
                    color="primary"
                    onClick={() => {

                      setSelectedTransaction(
                        transaction
                      );

                      setDialogOpen(true);
                    }}
                  >
                    <EditIcon />
                  </IconButton>

                </Tooltip>

                <Tooltip title="Delete transaction">

                  <IconButton
                    color="error"
                    onClick={() =>
                      void handleDeleteTransaction(transaction.id)
                    }
                  >
                    <DeleteIcon />
                  </IconButton>

                </Tooltip>

              </TableCell>

            </TableRow>

          ))
          )}
          
        </TableBody>

      </Table>

      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        rowsPerPage={size}
        rowsPerPageOptions={[5, 10, 25]}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />

    </TableContainer>


    <AddTransactionDialog

      open={dialogOpen}

      transaction={selectedTransaction}

      onClose={() => {
        setDialogOpen(false)
        setSelectedTransaction(null);
        }
      }

      onSaved={(message) => {

        void loadTransactions();

        setSnackbar({
          open: true,
          message: message,
          severity: "success",
        });
      }}

    />

    <Snackbar
      open={snackbar.open}
      autoHideDuration={3000}

      onClose={() =>
        setSnackbar({
          ...snackbar,
          open: false,
        })
      }
    >

      <Alert
        severity={snackbar.severity}

        onClose={() =>
          setSnackbar({
            ...snackbar,
            open: false,
          })
        }
      >
        {snackbar.message}
      </Alert>

    </Snackbar>

  </Box>
);
}
