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
comment|/**  *   * @org.apache.xbean.XBean element="mysql-jdbc-adapter"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MySqlJDBCAdapter
extends|extends
name|DefaultJDBCAdapter
block|{
comment|// The transactional types..
specifier|public
specifier|static
specifier|final
name|String
name|INNODB
init|=
literal|"INNODB"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NDBCLUSTER
init|=
literal|"NDBCLUSTER"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BDB
init|=
literal|"BDB"
decl_stmt|;
comment|// The non transactional types..
specifier|public
specifier|static
specifier|final
name|String
name|MYISAM
init|=
literal|"MYISAM"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ISAM
init|=
literal|"ISAM"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MERGE
init|=
literal|"MERGE"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HEAP
init|=
literal|"HEAP"
decl_stmt|;
name|String
name|engineType
init|=
name|INNODB
decl_stmt|;
specifier|public
name|void
name|setStatements
parameter_list|(
name|Statements
name|statements
parameter_list|)
block|{
name|String
name|type
init|=
name|engineType
operator|.
name|toUpperCase
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|INNODB
argument_list|)
operator|&&
operator|!
name|type
operator|.
name|equals
argument_list|(
name|NDBCLUSTER
argument_list|)
condition|)
block|{
comment|// Don't use LOCK TABLE for the INNODB and NDBCLUSTER engine types...
name|statements
operator|.
name|setLockCreateStatement
argument_list|(
literal|"LOCK TABLE "
operator|+
name|statements
operator|.
name|getFullLockTableName
argument_list|()
operator|+
literal|" WRITE"
argument_list|)
expr_stmt|;
block|}
name|statements
operator|.
name|setBinaryDataType
argument_list|(
literal|"LONGBLOB"
argument_list|)
expr_stmt|;
comment|// Update the create statements so they use the right type of engine
name|String
index|[]
name|s
init|=
name|statements
operator|.
name|getCreateSchemaStatements
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|s
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"CREATE TABLE"
argument_list|)
condition|)
block|{
name|s
index|[
name|i
index|]
operator|=
name|s
index|[
name|i
index|]
operator|+
literal|" TYPE="
operator|+
name|type
expr_stmt|;
block|}
block|}
name|super
operator|.
name|setStatements
argument_list|(
name|statements
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getEngineType
parameter_list|()
block|{
return|return
name|engineType
return|;
block|}
specifier|public
name|void
name|setEngineType
parameter_list|(
name|String
name|engineType
parameter_list|)
block|{
name|this
operator|.
name|engineType
operator|=
name|engineType
expr_stmt|;
block|}
block|}
end_class

end_unit

