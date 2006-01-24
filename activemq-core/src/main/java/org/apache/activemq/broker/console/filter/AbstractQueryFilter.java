begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractQueryFilter
implements|implements
name|QueryFilter
block|{
specifier|protected
name|QueryFilter
name|next
decl_stmt|;
comment|/**      * Creates a query filter, with the next filter specified by next.      * @param next - the next query filter      */
specifier|protected
name|AbstractQueryFilter
parameter_list|(
name|QueryFilter
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
comment|/**      * Performs a query given the query string      * @param query - query string      * @return objects that matches the query      * @throws Exception      */
specifier|public
name|List
name|query
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Converts string query to map query
name|StringTokenizer
name|tokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|query
argument_list|,
name|QUERY_DELIMETER
argument_list|)
decl_stmt|;
return|return
name|query
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
name|tokens
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

