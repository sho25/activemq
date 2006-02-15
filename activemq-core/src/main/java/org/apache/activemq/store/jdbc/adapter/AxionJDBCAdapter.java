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
name|StatementProvider
import|;
end_import

begin_comment
comment|/**  * Axion specific Adapter.  *   * Axion does not seem to support ALTER statements or sub-selects.  This means:  * - We cannot auto upgrade the schema was we roll out new versions of ActiveMQ  * - We cannot delete durable sub messages that have be acknowledged by all consumers.  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|AxionJDBCAdapter
extends|extends
name|StreamJDBCAdapter
block|{
specifier|public
specifier|static
name|StatementProvider
name|createStatementProvider
parameter_list|()
block|{
name|DefaultStatementProvider
name|answer
init|=
operator|new
name|DefaultStatementProvider
argument_list|()
block|{
specifier|public
name|String
index|[]
name|getCreateSchemaStatments
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"CREATE TABLE "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|messageTableName
operator|+
literal|"("
operator|+
literal|"ID "
operator|+
name|sequenceDataType
operator|+
literal|" NOT NULL"
operator|+
literal|", CONTAINER "
operator|+
name|containerNameDataType
operator|+
literal|", MSGID_PROD "
operator|+
name|msgIdDataType
operator|+
literal|", MSGID_SEQ "
operator|+
name|sequenceDataType
operator|+
literal|", EXPIRATION "
operator|+
name|longDataType
operator|+
literal|", MSG "
operator|+
operator|(
name|useExternalMessageReferences
condition|?
name|stringIdDataType
else|:
name|binaryDataType
operator|)
operator|+
literal|", PRIMARY KEY ( ID ) )"
block|,
literal|"CREATE INDEX "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|messageTableName
operator|+
literal|"_MIDX ON "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|messageTableName
operator|+
literal|" (MSGID_PROD,MSGID_SEQ)"
block|,
literal|"CREATE INDEX "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|messageTableName
operator|+
literal|"_CIDX ON "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|messageTableName
operator|+
literal|" (CONTAINER)"
block|,
literal|"CREATE INDEX "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|"_EIDX ON "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" (EXPIRATION)"
block|,
literal|"CREATE TABLE "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|"("
operator|+
literal|"CONTAINER "
operator|+
name|containerNameDataType
operator|+
literal|" NOT NULL"
operator|+
literal|", CLIENT_ID "
operator|+
name|stringIdDataType
operator|+
literal|" NOT NULL"
operator|+
literal|", SUB_NAME "
operator|+
name|stringIdDataType
operator|+
literal|" NOT NULL"
operator|+
literal|", SELECTOR "
operator|+
name|stringIdDataType
operator|+
literal|", LAST_ACKED_ID "
operator|+
name|sequenceDataType
operator|+
literal|", PRIMARY KEY ( CONTAINER, CLIENT_ID, SUB_NAME))"
block|,                                          }
return|;
block|}
specifier|public
name|String
name|getDeleteOldMessagesStatment
parameter_list|()
block|{
return|return
literal|"DELETE FROM "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|messageTableName
operator|+
literal|" WHERE ( EXPIRATION<>0 AND EXPIRATION<?)"
return|;
block|}
block|}
decl_stmt|;
name|answer
operator|.
name|setLongDataType
argument_list|(
literal|"LONG"
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|AxionJDBCAdapter
parameter_list|()
block|{
name|this
argument_list|(
name|createStatementProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AxionJDBCAdapter
parameter_list|(
name|StatementProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

