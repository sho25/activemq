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

begin_class
specifier|public
class|class
name|StubQueryFilter
implements|implements
name|QueryFilter
block|{
specifier|private
name|List
name|data
decl_stmt|;
comment|/**      * Creates a stub query that returns the given collections as the query result      * @param data - the stub query result      */
specifier|public
name|StubQueryFilter
parameter_list|(
name|List
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
comment|/**      * Returns the provided stub data as a stub query result      * @param queryStr - not use      * @return the stub query result      * @throws Exception      */
specifier|public
name|List
name|query
parameter_list|(
name|String
name|queryStr
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|data
return|;
block|}
comment|/**      * Returns the provided stub data as a stub query result      * @param queries - not use      * @return the stub query result      * @throws Exception      */
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
name|data
return|;
block|}
block|}
end_class

end_unit

