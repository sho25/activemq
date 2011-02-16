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
name|PreparedStatement
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

begin_comment
comment|/**  * This JDBCAdapter inserts and extracts BLOB data using the  * setBytes()/getBytes() operations. The databases/JDBC drivers that use this  * adapter are:  *   * @org.apache.xbean.XBean element="bytesJDBCAdapter"  *   */
end_comment

begin_class
specifier|public
class|class
name|BytesJDBCAdapter
extends|extends
name|DefaultJDBCAdapter
block|{
comment|/**      * @see org.apache.activemq.store.jdbc.adapter.DefaultJDBCAdapter#getBinaryData(java.sql.ResultSet,      *      int)      */
specifier|protected
name|byte
index|[]
name|getBinaryData
parameter_list|(
name|ResultSet
name|rs
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|rs
operator|.
name|getBytes
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.activemq.store.jdbc.adapter.DefaultJDBCAdapter#setBinaryData(java.sql.PreparedStatement,      *      int, byte[])      */
specifier|protected
name|void
name|setBinaryData
parameter_list|(
name|PreparedStatement
name|s
parameter_list|,
name|int
name|index
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|SQLException
block|{
name|s
operator|.
name|setBytes
argument_list|(
name|index
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

