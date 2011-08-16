begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|filter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|RegExQueryFilter
extends|extends
name|AbstractQueryFilter
block|{
specifier|public
specifier|static
specifier|final
name|String
name|REGEX_PREFIX
init|=
literal|"REGEX:QUERY:"
decl_stmt|;
comment|/**      * Creates a regular expression query that is able to match an object using      * key-value pattern regex filtering      *      * @param next      */
specifier|protected
name|RegExQueryFilter
parameter_list|(
name|QueryFilter
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
comment|/**      * Separates the regular expressions queries from the usual queries. A query      * is a regex query, if it is key-value pair with the format<key>=<value>,      * and value is a pattern that satisfies the isRegularExpression method.      *      * @param queries - list of queries      * @return filtered objects that matches the regex query      * @throws Exception      */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
name|List
name|query
parameter_list|(
name|List
name|queries
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
name|regex
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|List
name|newQueries
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|// Lets parse for regular expression queries
for|for
control|(
name|Iterator
name|i
init|=
name|queries
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
comment|// Get key-value pair
name|String
name|token
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
literal|""
decl_stmt|;
name|String
name|val
init|=
literal|""
decl_stmt|;
name|int
name|pos
init|=
name|token
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|>=
literal|0
condition|)
block|{
name|val
operator|=
name|token
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|key
operator|=
name|token
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|// Add the regex query to list and make it a non-factor in the
comment|// succeeding queries
if|if
condition|(
name|isRegularExpression
argument_list|(
name|val
argument_list|)
condition|)
block|{
name|regex
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|compileQuery
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add the normal query to the query list
block|}
else|else
block|{
name|newQueries
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Filter the result using the regular expressions specified
return|return
name|filterCollectionUsingRegEx
argument_list|(
name|regex
argument_list|,
name|next
operator|.
name|query
argument_list|(
name|newQueries
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Checks if a given string is a regular expression query. Currently, a      * pattern is a regex query, if it starts with the      * RegExQueryFilter.REGEX_PREFIX.      *      * @param query      * @return boolean result of query check      */
specifier|protected
name|boolean
name|isRegularExpression
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
name|query
operator|.
name|startsWith
argument_list|(
name|REGEX_PREFIX
argument_list|)
return|;
block|}
comment|/**      * Compiles the regex query to a pattern.      *      * @param query - query string to compile      * @return regex pattern      */
specifier|protected
name|Pattern
name|compileQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|query
operator|.
name|substring
argument_list|(
name|REGEX_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Filter the specified colleciton using the regex patterns extracted.      *      * @param regex - regex map      * @param data - list of objects to filter      * @return filtered list of objects that matches the regex map      * @throws Exception      */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|protected
name|List
name|filterCollectionUsingRegEx
parameter_list|(
name|Map
name|regex
parameter_list|,
name|List
name|data
parameter_list|)
throws|throws
name|Exception
block|{
comment|// No regular expressions filtering needed
if|if
condition|(
name|regex
operator|==
literal|null
operator|||
name|regex
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|data
return|;
block|}
name|List
name|filteredElems
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|// Get each data object to filter
for|for
control|(
name|Iterator
name|i
init|=
name|data
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|dataElem
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// If properties of data matches all the regex pattern, add it
if|if
condition|(
name|matches
argument_list|(
name|dataElem
argument_list|,
name|regex
argument_list|)
condition|)
block|{
name|filteredElems
operator|.
name|add
argument_list|(
name|dataElem
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filteredElems
return|;
block|}
comment|/**      * Determines how the object is to be matched to the regex map.      *      * @param data - object to match      * @param regex - regex map      * @return true, if the object matches the regex map, false otherwise      * @throws Exception      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|protected
specifier|abstract
name|boolean
name|matches
parameter_list|(
name|Object
name|data
parameter_list|,
name|Map
name|regex
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

