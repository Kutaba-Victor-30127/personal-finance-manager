import {
  useCallback,
  useEffect,
  useState,
} from "react";

import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Paper,
  Snackbar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";

import {
  createCategory,
  deleteCategory,
  getCategories,
  updateCategory,
} from "../services/categoryService";

import type { Category } from "../types/category";


export default function CategoriesPage() {

  const [categories, setCategories] =
    useState<Category[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState<string | null>(null);

  const [dialogOpen, setDialogOpen] =
    useState(false);

  const [categoryName, setCategoryName] =
    useState("");

  const [saving, setSaving] =
    useState(false);

  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);

  const [snackbar, setSnackbar] =
    useState({
      open: false,
      message: "",
      severity: "success" as
        "success" | "error",
    });


  const loadCategories =
    useCallback(async () => {

      try {

        setError(null);

        const data =
          await getCategories();

        setCategories(data);

      } catch (error) {

        console.error(
          "Failed to load categories:",
          error
        );

        setError(
          "Could not load categories."
        );

      } finally {

        setLoading(false);
      }

    }, []);


  useEffect(() => {

    void loadCategories();

  }, [loadCategories]);


  const handleSaveCategory =
    async () => {

      const name =
        categoryName.trim();

      if (!name) {
        return;
      }

      try {

        setSaving(true);

        if (selectedCategory) {

          await updateCategory(
            selectedCategory.id,
            name
          );

          setSnackbar({
            open: true,
            message:
              "Category updated successfully.",
            severity: "success",
          });

        } else {

          await createCategory(name);

          setSnackbar({
            open: true,
            message:
              "Category added successfully.",
            severity: "success",
          });
        }
        

        setCategoryName("");

        setSelectedCategory(null);

        setDialogOpen(false);

        await loadCategories();

      } catch (error) {

        console.error(
          "Failed to create category:",
          error
        );

        setSnackbar({
          open: true,
          message:
            "Failed to create category.",
          severity: "error",
        });

      } finally {

        setSaving(false);
      }
    };


  const handleDeleteCategory =
    async (id: number) => {

      const confirmed =
        window.confirm(
          "Are you sure you want to delete this category?"
        );

      if (!confirmed) {
        return;
      }

      try {

        await deleteCategory(id);

        await loadCategories();

        setSnackbar({
          open: true,
          message:
            "Category deleted successfully.",
          severity: "success",
        });

      } catch (error) {

        console.error(
          "Failed to delete category:",
          error
        );

        setSnackbar({
          open: true,
          message:
            "Could not delete category. It may be used by existing transactions.",
          severity: "error",
        });
      }
    };


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


  return (

    <Box>

      {/* HEADER */}

      <Box
        sx={{
          display: "flex",
          justifyContent:
            "space-between",
          alignItems: "center",
          mb: 4,
        }}
      >

        <Box>

          <Typography variant="h4">
            Categories
          </Typography>

          <Typography
            color="text.secondary"
          >
            Manage your transaction categories
          </Typography>

        </Box>


        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => {

            setSelectedCategory(null);

            setCategoryName("");

            setDialogOpen(true);
          }}
        >
          Add Category
        </Button>

      </Box>


      {/* TABLE */}

      <TableContainer
        component={Paper}
      >

        <Table>

          <TableHead>

            <TableRow>

              <TableCell>
                Name
              </TableCell>

              <TableCell
                align="right"
              >
                Actions
              </TableCell>

            </TableRow>

          </TableHead>


          <TableBody>

            {categories.length === 0 ? (

              <TableRow>

                <TableCell
                  colSpan={2}
                  align="center"
                  sx={{
                    py: 6,
                  }}
                >

                  <Typography
                    variant="h6"
                    sx={{
                      mb: 1,
                    }}
                  >
                    No categories found
                  </Typography>

                  <Typography
                    color="text.secondary"
                  >
                    Create your first category.
                  </Typography>

                </TableCell>

              </TableRow>

            ) : (

              categories.map(
                (category) => (

                  <TableRow
                    key={category.id}
                  >

                    <TableCell>

                      <Typography
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {category.name}
                      </Typography>

                    </TableCell>


                    <TableCell
                      align="right"
                    >

                      <Tooltip title="Edit category">

                        <IconButton
                          color="primary"
                          onClick={() => {

                            setSelectedCategory(category);

                            setCategoryName(category.name);

                            setDialogOpen(true);
                          }}
                        >
                          <EditIcon />
                        </IconButton>

                      </Tooltip>

                      <Tooltip
                        title="Delete category"
                      >

                        <IconButton
                          color="error"
                          onClick={() =>
                            void handleDeleteCategory(
                              category.id
                            )
                          }
                        >
                          <DeleteIcon />
                        </IconButton>

                      </Tooltip>

                    </TableCell>

                  </TableRow>

                )
              )

            )}

          </TableBody>

        </Table>

      </TableContainer>


      {/* ADD CATEGORY DIALOG */}

      <Dialog
        open={dialogOpen}
        onClose={() =>{
          
          setDialogOpen(false);

          setSelectedCategory(null);

          setCategoryName("");
        }}
        fullWidth
        maxWidth="xs"
      >

        <DialogTitle>
          
          { selectedCategory ? 
            "Edit Category" : 
            "Add Category"
          }

        </DialogTitle>


        <DialogContent>

          <TextField
            label="Category name"
            fullWidth
            autoFocus
            sx={{
              mt: 1,
            }}
            value={categoryName}

            onChange={(event) => {

              setCategoryName(
                event.target.value
              );

            }}
          />

        </DialogContent>


        <DialogActions>

          <Button
            onClick={() => {

              setDialogOpen(false);

              setSelectedCategory(null);

              setCategoryName("");
            }}
            disabled={saving}
          >
            Cancel
          </Button>


          <Button
            variant="contained"
            disabled={
              saving ||
              categoryName.trim() === ""
            }
            onClick={() =>
              void handleSaveCategory()
            }
          >

            {saving
              ? "Saving..."
              : selectedCategory
                ? "Update"
                : "Add"
            }

          </Button>

        </DialogActions>

      </Dialog>


      {/* SNACKBAR */}

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
          severity={
            snackbar.severity
          }

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