package com.indiareads.retailapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 9/11/2015.
 */
public class Categories {

    public static List<String>  superCategoryList;
    public static List<Integer> superCategoryListId;

    private static List<String> literature;
    private static List<String> fiction;
    private static List<String> childrenAndTeens;
    private static List<String> boardExamPrep;
    private static List<String> technologyAndEngineering;
    private static List<String> businessAndEconomics;
    private static List<String> nonFiction;
    private static List<String> otherTextBooks;


    private static List<Integer> literatureId;
    private static List<Integer> fictionId;
    private static List<Integer> childrenAndTeensId;
    private static List<Integer> boardExamPrepId;
    private static List<Integer> technologyAndEngineeringId;
    private static List<Integer> businessAndEconomicsId;
    private static List<Integer> nonFictionId;
    private static List<Integer> otherTextBooksId;


    public static List<String> getSuperCategoryList(){
        if(superCategoryList ==null){
            initializeSuperCategoryList();
        }
        return superCategoryList;
    }

    public static List<Integer> getSuperCategoryListId(){
        if(superCategoryListId ==null){
            initializeSuperCategoryListId();
        }
        return superCategoryListId;
    }

    public static List<String> getOtherTextBooks(){
        if(otherTextBooks==null){
            initializeOtherTextBooksList();
        }
        return otherTextBooks;
    }

    public static List<Integer> getOtherTextBooksId(){
        if(otherTextBooksId==null){
            initializeOtherTextBooksListId();
        }
        return otherTextBooksId;
    }

    public static List<String> getNonFiction(){
        if(nonFiction==null){
            initializeNonFictionList();
        }
        return nonFiction;
    }

    public static List<Integer> getNonFictionId(){
        if(nonFictionId==null){
            initializeNonFictionListId();
        }
        return nonFictionId;
    }


    public static List<String> getBusinessAndEconomics(){
        if(businessAndEconomics==null){
            initializeBusinessAndEconomicsList();
        }
        return businessAndEconomics;
    }

    public static List<Integer> getBusinessAndEconomicsId(){
        if(businessAndEconomicsId==null){
            initializeBusinessAndEconomicsListId();
        }
        return businessAndEconomicsId;
    }


    public static List<String> getTechnologyAndEngineering(){
        if(technologyAndEngineering==null){
            initializeTechnologyAndEngineeringList();
        }
        return technologyAndEngineering;
    }

    public static List<Integer> getTechnologyAndEngineeringId(){
        if(technologyAndEngineeringId==null){
            initializeTechnologyAndEngineeringListId();
        }
        return technologyAndEngineeringId;
    }

    public static List<String> getBoardExamPrep(){
        if(boardExamPrep==null){
            initializeBoardExamPrepList();
        }
        return boardExamPrep;
    }

    public static List<Integer> getBoardExamPrepId(){
        if(boardExamPrepId==null){
            initializeBoardExamPrepListId();
        }
        return boardExamPrepId;
    }


    public static List<String> getChildrenAndTeens(){
        if(childrenAndTeens==null){
            initializeChildrenAndTeensList();
        }
        return childrenAndTeens;
    }

    public static List<Integer> getChildrenAndTeensId(){
        if(childrenAndTeensId==null){
            initializeChildrenAndTeensListId();
        }
        return childrenAndTeensId;
    }

    public static List<String> getFictionList(){
        if(fiction==null){
            initializeFictionList();
        }
        return fiction;
    }

    public static List<Integer> getFictionListId(){
        if(fictionId==null){
            initializeFictionListId();
        }
        return fictionId;
    }

    public static List<String> getLiteratureList(){
        if(literature==null){
            initializeLiteratureList();
        }
        return literature;
    }


    public static List<Integer> getLiteratureListId(){
        if(literatureId==null){
            initializeLiteratureListId();
        }
        return literatureId;
    }




    private static void   initializeSuperCategoryList(){

        superCategoryList =new ArrayList<>();

        superCategoryList.add("Literature");
        superCategoryList.add("Fiction");
        superCategoryList.add("Children & Teens");
        superCategoryList.add("Board & Exam Prep");
        superCategoryList.add("Technology & Engineering");
        superCategoryList.add("Non Fiction");
        superCategoryList.add("Business & Economics");
        superCategoryList.add("Other Text Books");
    }

    private static void   initializeSuperCategoryListId(){

        superCategoryListId =new ArrayList<>();

        superCategoryListId.add(1);
        superCategoryListId.add(2);
        superCategoryListId.add(3);
        superCategoryListId.add(4);
        superCategoryListId.add(5);
        superCategoryListId.add(6);
        superCategoryListId.add(7);
        superCategoryListId.add(8);
    }




    private static void   initializeOtherTextBooksList(){

        otherTextBooks=new ArrayList<>();

        otherTextBooks.add("All");
        otherTextBooks.add("Study Aid");
        otherTextBooks.add("Academic & Reference");
        otherTextBooks.add("Political Science");
        otherTextBooks.add("Social Science");
        otherTextBooks.add("Arts");
        otherTextBooks.add("Law");
        otherTextBooks.add("Performing Arts");
        otherTextBooks.add("Philosophy");
        otherTextBooks.add("Psychology");
        otherTextBooks.add("Medical");
        otherTextBooks.add("Architecture");
        otherTextBooks.add("Design");
        otherTextBooks.add("Foreign Language Study");
     //   otherTextBooks.add("Body, Mind & Spirit");------------------

    }
    //146741

    private static void   initializeOtherTextBooksListId(){

        otherTextBooksId=new ArrayList<>();

        otherTextBooksId.add(8);
        otherTextBooksId.add(56);
        otherTextBooksId.add(74);
        otherTextBooksId.add(20);
        otherTextBooksId.add(42);
        otherTextBooksId.add(7);
        otherTextBooksId.add(30);
        otherTextBooksId.add(36);
        otherTextBooksId.add(52);
        otherTextBooksId.add(35);
        otherTextBooksId.add(19);
        otherTextBooksId.add(32);
        otherTextBooksId.add(54);
        otherTextBooksId.add(44);
        //    otherTextBooksId.add(25);


    }



    private static void   initializeNonFictionList(){

        nonFiction=new ArrayList<>();

        nonFiction.add("All");
        nonFiction.add("Biography and Autobiography");
        nonFiction.add("Health & Fitness");
        nonFiction.add("History");
        nonFiction.add("Nature");
        nonFiction.add("Non-Classifiable");
        nonFiction.add("Pets");
        nonFiction.add("Photography");
        nonFiction.add("Travel");
        nonFiction.add("Transportation");
        nonFiction.add("Sports & Recreation");
        nonFiction.add("Religion");
        nonFiction.add("Reference");
        nonFiction.add("Music");
        nonFiction.add("House & Home");
        nonFiction.add("Gardening");
        nonFiction.add("Family & Relationships");
        nonFiction.add("European - German");
        nonFiction.add("Crafts & Hobbies");
        nonFiction.add("Cooking");
        nonFiction.add("Bibles");
        nonFiction.add("Antiques & Collectibles");
        nonFiction.add("Self-Help");
    }

    private static void   initializeNonFictionListId(){

        nonFictionId=new ArrayList<>();

        nonFictionId.add(6);
        nonFictionId.add(33);
        nonFictionId.add(31);
        nonFictionId.add(10);
        nonFictionId.add(47);
        nonFictionId.add(75);
        nonFictionId.add(46);
        nonFictionId.add(34);
        nonFictionId.add(28);
        nonFictionId.add(51);
        nonFictionId.add(24);
        nonFictionId.add(49);
        nonFictionId.add(37);
        nonFictionId.add(17);
        nonFictionId.add(55);
        nonFictionId.add(41);
        nonFictionId.add(27);
        nonFictionId.add(81);
        nonFictionId.add(38);
        nonFictionId.add(29);
        nonFictionId.add(40);
        nonFictionId.add(11);
        nonFictionId.add(14);
    }


    private static void  initializeBusinessAndEconomicsList(){

        businessAndEconomics=new ArrayList<>();

        businessAndEconomics.add("All");
        businessAndEconomics.add("Business & Economics");


    }

    private static void   initializeBusinessAndEconomicsListId(){

        businessAndEconomicsId=new ArrayList<>();

        businessAndEconomicsId.add(7);
        businessAndEconomicsId.add(22);


    }




    private static void   initializeTechnologyAndEngineeringList(){

        technologyAndEngineering=new ArrayList<>();

        technologyAndEngineering.add("All");
        technologyAndEngineering.add("Technology");
        technologyAndEngineering.add("Technology & Engineering");
        technologyAndEngineering.add("Mathematics");
        technologyAndEngineering.add("Science");
        technologyAndEngineering.add("Computers");

    }



    private static void   initializeTechnologyAndEngineeringListId(){

        technologyAndEngineeringId=new ArrayList<>();

        technologyAndEngineeringId.add(5);
        technologyAndEngineeringId.add(16);
        technologyAndEngineeringId.add(43);
        technologyAndEngineeringId.add(23);
        technologyAndEngineeringId.add(15);
        technologyAndEngineeringId.add(4);

    }


    private static void   initializeBoardExamPrepList(){

        boardExamPrep=new ArrayList<>();

        boardExamPrep.add("All");
        boardExamPrep.add("Competitive Exams");
        boardExamPrep.add("Board Exams");
        boardExamPrep.add("Education");
    }

    private static void   initializeBoardExamPrepListId(){

        boardExamPrepId=new ArrayList<>();

        boardExamPrepId.add(4);
        boardExamPrepId.add(1);
        boardExamPrepId.add(5);
        boardExamPrepId.add(26);
    }


    private static void   initializeChildrenAndTeensList(){

        childrenAndTeens=new ArrayList<>();

        childrenAndTeens.add("All");
        childrenAndTeens.add("Juvenile Fiction");
        childrenAndTeens.add("Juvenile NonFiction");
        childrenAndTeens.add("Games");
        childrenAndTeens.add("Comics & Graphic Novels");

    }

    private static void   initializeChildrenAndTeensListId(){

        childrenAndTeensId=new ArrayList<>();

        childrenAndTeensId.add(3);
        childrenAndTeensId.add(13);
        childrenAndTeensId.add(21);
        childrenAndTeensId.add(53);
        childrenAndTeensId.add(8);

    }



    private static void   initializeFictionList(){

        fiction=new ArrayList<>();

        fiction.add("All");
        fiction.add("Fiction");
        fiction.add("Mystery & Thriller");
        fiction.add("Romance");
        fiction.add("True Crime");
        fiction.add("Humor");

    }

    private static void  initializeFictionListId(){

        fictionId=new ArrayList<>();

        fictionId.add(2);
        fictionId.add(6);
        fictionId.add(78);
        fictionId.add(77);
        fictionId.add(48);
        fictionId.add(45);

    }


    private static void   initializeLiteratureList(){

        literature=new ArrayList<>();
        literature.add("All");
        literature.add("Literacy Collections");
        literature.add("Literacy Criticism");
        literature.add("Literature & Fiction");
        literature.add("Poetry");
        literature.add("Drama");

    }

    private static void   initializeLiteratureListId(){

        literatureId=new ArrayList<>();

        literatureId.add(1);
        literatureId.add(39);
        literatureId.add(50);
        literatureId.add(76);
        literatureId.add(9);
        literatureId.add(12);

    }




}
