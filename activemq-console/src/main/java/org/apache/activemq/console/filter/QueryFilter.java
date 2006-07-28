begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|List
import|;
end_import

begin_interface
specifier|public
interface|interface
name|QueryFilter
block|{
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_DELIMETER
init|=
literal|","
decl_stmt|;
comment|/**      * Interface for querying      * @param queryStr - the query string      * @return collection of objects that satisfies the query      * @throws Exception      */
specifier|public
name|List
name|query
parameter_list|(
name|String
name|queryStr
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Interface for querying      * @param queries - list of individual queries      * @return collection of objects that satisfies the query      * @throws Exception      */
specifier|public
name|List
name|query
parameter_list|(
name|List
name|queries
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

