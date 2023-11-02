package com.cocoiland.pricehistory.constants;

public final class Constants {
    public static final String SYSTEM = "System";
    public static final String ADMIN = "Admin";
    public static final Integer PRODUCT_DETAILS_UPDATE_INTERVAL_IN_DAYS = 7;
    public static final Integer PRICE_UPDATE_INTERVAL_IN_HOURS = 0;

    public static final String PRODUCT_ID = "product_id";
    public static final String FROM_DATE = "from_date";
    public static final String TO_DATE = "to_date";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String INPUT_NULL = "Input can't be null";
    public static final String INPUT_EMPTY = "Input can't be empty";
    public static final String CANT_BE_NULL= " can't be null";
    public static final String CANT_BE_EMPTY = " can't be empty";
    public static final String INPUT = "input";
    public static final String DATE = "date";
    public static final String PRICE = "price";
    public static final String IMAGE_URL = "image_url";
    public static final String PRICE_HISTORY = "price_history";
    public static final String PID = "pid";
    public static final String PLATFORM = "platform";
    public static final String UNDERSCORE = "_";
    public static final String PRODUCTID = "productId";
    public static final String SPACE = " ";
    public static final String LSP = "lsp";


    //Errors & Exceptions
    public static final String ES_ERROR_OCCURRED = "Request couldn't be executed...Please try again!";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong!";
    public static final String ERROR_FETCHING_PRICE_HISTORY_FROM_ES = "Error while fetching price history data from ES";
    public static final String ERROR_ADDING_PRODUCT_DETAILS_TO_ES = "Error while adding product details to ES";
    public static final String ERROR_UPDATING_PRODUCT_DETAILS_TO_ES = "Error while updating product details to ES";
    public static final String ERROR_ADDING_PRODUCT_PRICE_DETAILS_TO_ES = "Error while adding product price details to ES";
    public static final String ERROR_FETCHING_PROCESSING_PRODUCT_DETAILS = "Error fetching/Processing product details";
    public static final String ERROR_FETCHING_PRODUCT_DETAILS = "Error fetching product details";

    public static final String COULD_NOT_FIND_PRODUCT_DETAILS = "Couldn't find the requested product details";
    public static final String UNSUPPORTED_SHOPPING_SITE = "Unsupported shopping site";
    public static final String ERROR_FETCHING_PRODUCT_PRICE_HISTORY_DATA = "Error fetching price history data!";
}
