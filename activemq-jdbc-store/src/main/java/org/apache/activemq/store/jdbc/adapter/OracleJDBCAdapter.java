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
comment|/**  * Implements all the default JDBC operations that are used  * by the JDBCPersistenceAdapter.  *<p/>  * Subclassing is encouraged to override the default  * implementation of methods to account for differences  * in JDBC Driver implementations.  *<p/>  *  * @org.apache.xbean.XBean element="oracleJDBCAdapter"  *  *   */
end_comment

begin_class
specifier|public
class|class
name|OracleJDBCAdapter
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
name|statements
operator|.
name|setLongDataType
argument_list|(
literal|"NUMBER"
argument_list|)
expr_stmt|;
name|statements
operator|.
name|setSequenceDataType
argument_list|(
literal|"NUMBER"
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
annotation|@
name|Override
specifier|public
name|String
name|limitQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
literal|"SELECT * FROM ("
operator|+
name|query
operator|+
literal|") WHERE ROWNUM<= "
operator|+
name|getMaxRows
argument_list|()
return|;
block|}
block|}
end_class

end_unit

