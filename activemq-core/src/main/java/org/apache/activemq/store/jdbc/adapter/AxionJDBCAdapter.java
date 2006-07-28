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
comment|/**  * Axion specific Adapter.  *   * Axion does not seem to support ALTER statements or sub-selects.  This means:  * - We cannot auto upgrade the schema was we roll out new versions of ActiveMQ  * - We cannot delete durable sub messages that have be acknowledged by all consumers.  *   * @org.apache.xbean.XBean element="axionJDBCAdapter"  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|AxionJDBCAdapter
extends|extends
name|StreamJDBCAdapter
block|{
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
name|setCreateSchemaStatements
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"CREATE TABLE "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|"("
operator|+
literal|"ID "
operator|+
name|statements
operator|.
name|getSequenceDataType
argument_list|()
operator|+
literal|" NOT NULL"
operator|+
literal|", CONTAINER "
operator|+
name|statements
operator|.
name|getContainerNameDataType
argument_list|()
operator|+
literal|", MSGID_PROD "
operator|+
name|statements
operator|.
name|getMsgIdDataType
argument_list|()
operator|+
literal|", MSGID_SEQ "
operator|+
name|statements
operator|.
name|getSequenceDataType
argument_list|()
operator|+
literal|", EXPIRATION "
operator|+
name|statements
operator|.
name|getLongDataType
argument_list|()
operator|+
literal|", MSG "
operator|+
operator|(
name|statements
operator|.
name|isUseExternalMessageReferences
argument_list|()
condition|?
name|statements
operator|.
name|getStringIdDataType
argument_list|()
else|:
name|statements
operator|.
name|getBinaryDataType
argument_list|()
operator|)
operator|+
literal|", PRIMARY KEY ( ID ) )"
block|,
literal|"CREATE INDEX "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|"_MIDX ON "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|" (MSGID_PROD,MSGID_SEQ)"
block|,
literal|"CREATE INDEX "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|"_CIDX ON "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|" (CONTAINER)"
block|,
literal|"CREATE INDEX "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|"_EIDX ON "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|" (EXPIRATION)"
block|,
literal|"CREATE TABLE "
operator|+
name|statements
operator|.
name|getFullAckTableName
argument_list|()
operator|+
literal|"("
operator|+
literal|"CONTAINER "
operator|+
name|statements
operator|.
name|getContainerNameDataType
argument_list|()
operator|+
literal|" NOT NULL"
operator|+
literal|", CLIENT_ID "
operator|+
name|statements
operator|.
name|getStringIdDataType
argument_list|()
operator|+
literal|" NOT NULL"
operator|+
literal|", SUB_NAME "
operator|+
name|statements
operator|.
name|getStringIdDataType
argument_list|()
operator|+
literal|" NOT NULL"
operator|+
literal|", SELECTOR "
operator|+
name|statements
operator|.
name|getStringIdDataType
argument_list|()
operator|+
literal|", LAST_ACKED_ID "
operator|+
name|statements
operator|.
name|getSequenceDataType
argument_list|()
operator|+
literal|", PRIMARY KEY ( CONTAINER, CLIENT_ID, SUB_NAME))"
block|,                     }
argument_list|)
expr_stmt|;
name|statements
operator|.
name|setDeleteOldMessagesStatement
argument_list|(
literal|"DELETE FROM "
operator|+
name|statements
operator|.
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE ( EXPIRATION<>0 AND EXPIRATION<?)"
argument_list|)
expr_stmt|;
name|statements
operator|.
name|setLongDataType
argument_list|(
literal|"LONG"
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

