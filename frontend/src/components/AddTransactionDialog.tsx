import { useEffect, useState } from "react";

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Stack,
  TextField,
} from "@mui/material";

import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

import { getCategories } from "../services/categoryService";
import { createTransaction, updateTransaction } from "../services/transactionService";

import type { Category } from "../types/category";
import type { Transaction } from "../types/transaction";

const transactionSchema = z.object({

  title: z
    .string()
    .min(1, "Title is required"),

  description: z.string(),

  amount: z
    .number()
    .positive("Amount must be greater than 0"),

  date: z
    .string()
    .min(1, "Date is required"),

  type: z.enum([
    "INCOME",
    "EXPENSE"
  ]),

  categoryId: z
    .number()
    .positive("Category is required"),

});

type TransactionFormData = z.infer<typeof transactionSchema>;

interface AddTransactionDialogProps{

    open: boolean;
    
    onClose: () => void;

    onSaved: (message: string) => void;
    
    transaction?: Transaction | null;
}


export default function AddTransactionDialog({
    open,
    onClose,
    onSaved,
    transaction,
}: AddTransactionDialogProps) {

    const [categories, setCategories] = useState<Category[]>([]);

    const {
        register,
        handleSubmit,
        reset,
        formState: {
            errors,
            isSubmitting
        },
    } = useForm<TransactionFormData>({

        resolver:
            zodResolver(transactionSchema),

        defaultValues: {
            title: "",
            description: "",
            amount: 0,
            date: "",
            type: "EXPENSE",
            categoryId: 0,
            },
    });

    useEffect(() => {

        if (!open){
            return;
        }

        const loadCategories = async () => {

            try{

                const data = await getCategories();

                setCategories(data);

                if (transaction) {

                  const category = data.find(
                    (category) => category.name === transaction.categoryName
                  );

                  reset({
                  title: transaction.title,
                  description: transaction.description,
                  amount: transaction.amount,
                  date: transaction.date,
                  type: transaction.type,
                  categoryId: category?.id ?? 0,
                  });

                } else {

                  reset({
                    title: "",
                    description: "",  
                    amount: 0,
                    date: "",
                    type: "EXPENSE",
                    categoryId: 0,
                    });

                }

            }catch(error) {

                console.error(
                    "Failed to load categories:",
                    error
                );
            }
        };

        void loadCategories();

    }, [open, transaction, reset]);

    const onSubmit = async (
        data: TransactionFormData
    ) => {

        try {

        let message: string;
        
        if (transaction){
          
          await updateTransaction(transaction.id, data);

          message = "Transaction updated successfully.";

        } else {

          await createTransaction(data);

          message = "Transaction created successfully.";

        }

        reset();

        onSaved(message);  

        onClose();

        } catch (error) {

        console.error(
            "Failed to save transaction:",
            error
        );
        }
    }; 


  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="sm"
    >

      <DialogTitle>
        {transaction ? "Edit Transaction" : "Add Transaction"}
      </DialogTitle>


      <form onSubmit={handleSubmit(onSubmit)}>

        <DialogContent>

          <Stack spacing={3} sx={{ mt: 1 }}>

            <TextField
              label="Title"
              fullWidth

              {...register("title")}

              error={!!errors.title}

              helperText={
                errors.title?.message
              }
            />


            <TextField
              label="Description"
              fullWidth

              {...register("description")}
            />


            <TextField
              label="Amount"
              type="number"
              fullWidth

              {...register(
                "amount",
                {
                  valueAsNumber: true
                }
              )}

              error={!!errors.amount}

              helperText={
                errors.amount?.message
              }
            />


            <TextField
              label="Date"
              type="date"
              fullWidth
              slotProps={{
                inputLabel: {
                  shrink: true,
                },
              }}

              {...register("date")}

              error={!!errors.date}

              helperText={
                errors.date?.message
              }
            />


            <TextField
              select
              label="Type"
              fullWidth

              {...register("type")}
            >

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
              fullWidth

              {...register(
                "categoryId",
                {
                  valueAsNumber: true
                }
              )}

              error={
                !!errors.categoryId
              }

              helperText={
                errors.categoryId?.message
              }
            >

              {categories.map(
                (category) => (

                  <MenuItem
                    key={category.id}
                    value={category.id}
                  >
                    {category.name}
                  </MenuItem>

                )
              )}

            </TextField>

          </Stack>

        </DialogContent>


        <DialogActions>

          <Button
            onClick={onClose}
            disabled={isSubmitting}
          >
            Cancel
          </Button>

          <Button
            type="submit"
            variant="contained"
            disabled={isSubmitting}
          >
            {transaction ? "Save changes" : "Add"}
          </Button>

        </DialogActions>

      </form>

    </Dialog>
  );
} 

