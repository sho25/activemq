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
name|usecases
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|JDBCDurableSubscriptionTest
extends|extends
name|DurableSubscriptionTestSupport
block|{
specifier|protected
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|()
throws|throws
name|IOException
block|{
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|jdbc
operator|.
name|setCleanupPeriod
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// set up small cleanup period
return|return
name|jdbc
return|;
block|}
block|}
end_class

end_unit

