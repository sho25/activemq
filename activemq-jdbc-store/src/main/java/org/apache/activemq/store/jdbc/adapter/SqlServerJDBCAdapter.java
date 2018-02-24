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
name|store
operator|.
name|jdbc
operator|.
name|adapter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|Statements
import|;
end_import

begin_comment
comment|/**  *   * @org.apache.xbean.XBean element="sqlServerJDBCAdapter"  */
end_comment

begin_class
specifier|public
class|class
name|SqlServerJDBCAdapter
extends|extends
name|DefaultJDBCAdapter
block|{
annotation|@
name|Override
specifier|public
name|void
name|setStatements
parameter_list|(
name|Statements
name|statements
parameter_list|)
block|{
name|String
name|lockCreateStatement
init|=
literal|"SELECT * FROM ACTIVEMQ_LOCK WITH (UPDLOCK, ROWLOCK) WHERE ID=1"
decl_stmt|;
name|statements
operator|.
name|setLockCreateStatement
argument_list|(
name|lockCreateStatement
argument_list|)
expr_stmt|;
name|statements
operator|.
name|setBinaryDataType
argument_list|(
literal|"VARBINARY(MAX)"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setStatements
argument_list|(
name|statements
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

