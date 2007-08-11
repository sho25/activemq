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

begin_class
specifier|public
class|class
name|WildcardToMsgSelectorTransformFilter
extends|extends
name|WildcardTransformFilter
block|{
comment|/**      * Creates a filter that is able to transform a wildcard query to a message      * selector format      *       * @param next - next query filter      */
specifier|public
name|WildcardToMsgSelectorTransformFilter
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
comment|/**      * Use to determine if a query string is a wildcard query. A query string is      * a wildcard query if it is a key-value pair with the format<key>=<value>      * and the value is enclosed in '' and contains '*' and '?'.      *       * @param query - query string      * @return true, if the query string is a wildcard query, false otherwise      */
specifier|protected
name|boolean
name|isWildcardQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
comment|// If the query is a key=value pair
name|String
name|key
init|=
name|query
decl_stmt|;
name|String
name|val
init|=
literal|""
decl_stmt|;
name|int
name|pos
init|=
name|key
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
name|key
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// If the value contains wildcards and is enclose by '
return|return
name|val
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
operator|&&
name|val
operator|.
name|endsWith
argument_list|(
literal|"'"
argument_list|)
operator|&&
operator|(
operator|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"*"
argument_list|)
operator|>=
literal|0
operator|)
operator|||
operator|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"?"
argument_list|)
operator|>=
literal|0
operator|)
operator|)
return|;
block|}
comment|/**      * Transform a wildcard query to message selector format      *       * @param query - query string to transform      * @return message selector format string      */
specifier|protected
name|String
name|transformWildcardQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
comment|// If the query is a key=value pair
name|String
name|key
init|=
name|query
decl_stmt|;
name|String
name|val
init|=
literal|""
decl_stmt|;
name|int
name|pos
init|=
name|key
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
name|key
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
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
name|val
operator|=
name|val
operator|.
name|replaceAll
argument_list|(
literal|"[?]"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|val
operator|=
name|val
operator|.
name|replaceAll
argument_list|(
literal|"[*]"
argument_list|,
literal|"%"
argument_list|)
expr_stmt|;
return|return
name|key
operator|+
literal|" LIKE "
operator|+
name|val
return|;
block|}
block|}
end_class

end_unit

