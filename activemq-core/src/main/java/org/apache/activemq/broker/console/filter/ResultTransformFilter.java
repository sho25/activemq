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
name|ArrayList
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

begin_class
specifier|public
specifier|abstract
class|class
name|ResultTransformFilter
implements|implements
name|QueryFilter
block|{
specifier|private
name|QueryFilter
name|next
decl_stmt|;
comment|/**      * Contructs a query filter that transform the format of the query result      * @param next - the query filter to retrieve the results from      */
specifier|protected
name|ResultTransformFilter
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
comment|/**      * Transforms the queried results to a collection of different objects      * @param query - the query string      * @return collections of transformed objects      * @throws Exception      */
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
return|return
name|transformList
argument_list|(
name|next
operator|.
name|query
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Transforms the queried results to a collection of different objects      * @param queries - the query map      * @return collections of transformed objects      * @throws Exception      */
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
return|return
name|transformList
argument_list|(
name|next
operator|.
name|query
argument_list|(
name|queries
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Transforms a collection to a collection of different objects.      * @param result - the collection to transform      * @return collection of properties objects      */
specifier|protected
name|List
name|transformList
parameter_list|(
name|List
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|List
name|props
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|result
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
name|props
operator|.
name|add
argument_list|(
name|transformElement
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
comment|/**      * Transform a result object      * @param obj - the object instance to transform      * @return the transformed object      */
specifier|protected
specifier|abstract
name|Object
name|transformElement
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

