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
comment|/**  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|DefaultStatementProvider
implements|implements
name|StatementProvider
block|{
specifier|private
name|String
name|tablePrefix
init|=
literal|""
decl_stmt|;
specifier|protected
name|String
name|messageTableName
init|=
literal|"ACTIVEMQ_MSGS"
decl_stmt|;
specifier|protected
name|String
name|durableSubAcksTableName
init|=
literal|"ACTIVEMQ_ACKS"
decl_stmt|;
specifier|protected
name|String
name|binaryDataType
init|=
literal|"BLOB"
decl_stmt|;
specifier|protected
name|String
name|containerNameDataType
init|=
literal|"VARCHAR(250)"
decl_stmt|;
specifier|protected
name|String
name|xidDataType
init|=
literal|"VARCHAR(250)"
decl_stmt|;
specifier|protected
name|String
name|msgIdDataType
init|=
literal|"VARCHAR(250)"
decl_stmt|;
specifier|protected
name|String
name|sequenceDataType
init|=
literal|"INTEGER"
decl_stmt|;
specifier|protected
name|String
name|longDataType
init|=
literal|"BIGINT"
decl_stmt|;
specifier|protected
name|String
name|stringIdDataType
init|=
literal|"VARCHAR(250)"
decl_stmt|;
specifier|protected
name|boolean
name|useExternalMessageReferences
init|=
literal|false
decl_stmt|;
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
name|getFullMessageTableName
argument_list|()
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
name|getFullMessageTableName
argument_list|()
operator|+
literal|"_MIDX ON "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" (MSGID_PROD,MSGID_SEQ)"
block|,
literal|"CREATE INDEX "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|"_CIDX ON "
operator|+
name|getFullMessageTableName
argument_list|()
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
block|,         }
return|;
block|}
specifier|public
name|String
name|getFullMessageTableName
parameter_list|()
block|{
return|return
name|getTablePrefix
argument_list|()
operator|+
name|messageTableName
return|;
block|}
specifier|public
name|String
index|[]
name|getDropSchemaStatments
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"DROP TABLE "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|""
block|,
literal|"DROP TABLE "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|""
block|,         }
return|;
block|}
specifier|public
name|String
name|getAddMessageStatment
parameter_list|()
block|{
return|return
literal|"INSERT INTO "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|"(ID, MSGID_PROD, MSGID_SEQ, CONTAINER, EXPIRATION, MSG) VALUES (?, ?, ?, ?, ?, ?)"
return|;
block|}
specifier|public
name|String
name|getUpdateMessageStatment
parameter_list|()
block|{
return|return
literal|"UPDATE "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" SET MSG=? WHERE ID=?"
return|;
block|}
specifier|public
name|String
name|getRemoveMessageStatment
parameter_list|()
block|{
return|return
literal|"DELETE FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE ID=?"
return|;
block|}
specifier|public
name|String
name|getFindMessageSequenceIdStatment
parameter_list|()
block|{
return|return
literal|"SELECT ID FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE MSGID_PROD=? AND MSGID_SEQ=?"
return|;
block|}
specifier|public
name|String
name|getFindMessageStatment
parameter_list|()
block|{
return|return
literal|"SELECT MSG FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE ID=?"
return|;
block|}
specifier|public
name|String
name|getFindAllMessagesStatment
parameter_list|()
block|{
return|return
literal|"SELECT ID, MSG FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=? ORDER BY ID"
return|;
block|}
specifier|public
name|String
name|getFindLastSequenceIdInMsgs
parameter_list|()
block|{
return|return
literal|"SELECT MAX(ID) FROM "
operator|+
name|getFullMessageTableName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getFindLastSequenceIdInAcks
parameter_list|()
block|{
return|return
literal|"SELECT MAX(LAST_ACKED_ID) FROM "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
return|;
block|}
specifier|public
name|String
name|getCreateDurableSubStatment
parameter_list|()
block|{
return|return
literal|"INSERT INTO "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|"(CONTAINER, CLIENT_ID, SUB_NAME, SELECTOR, LAST_ACKED_ID) "
operator|+
literal|"VALUES (?, ?, ?, ?, ?)"
return|;
block|}
specifier|public
name|String
name|getFindDurableSubStatment
parameter_list|()
block|{
return|return
literal|"SELECT SELECTOR, SUB_NAME "
operator|+
literal|"FROM "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|" WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?"
return|;
block|}
specifier|public
name|String
name|getFindAllDurableSubsStatment
parameter_list|()
block|{
return|return
literal|"SELECT SELECTOR, SUB_NAME, CLIENT_ID"
operator|+
literal|" FROM "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|" WHERE CONTAINER=?"
return|;
block|}
specifier|public
name|String
name|getUpdateLastAckOfDurableSub
parameter_list|()
block|{
return|return
literal|"UPDATE "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|" SET LAST_ACKED_ID=?"
operator|+
literal|" WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?"
return|;
block|}
specifier|public
name|String
name|getDeleteSubscriptionStatment
parameter_list|()
block|{
return|return
literal|"DELETE FROM "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|" WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?"
return|;
block|}
specifier|public
name|String
name|getFindAllDurableSubMessagesStatment
parameter_list|()
block|{
return|return
literal|"SELECT M.ID, M.MSG FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" M, "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|" D "
operator|+
literal|" WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=?"
operator|+
literal|" AND M.CONTAINER=D.CONTAINER AND M.ID> D.LAST_ACKED_ID"
operator|+
literal|" ORDER BY M.ID"
return|;
block|}
specifier|public
name|String
name|getFindAllDestinationsStatment
parameter_list|()
block|{
return|return
literal|"SELECT DISTINCT CONTAINER FROM "
operator|+
name|getFullMessageTableName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getRemoveAllMessagesStatment
parameter_list|()
block|{
return|return
literal|"DELETE FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=?"
return|;
block|}
specifier|public
name|String
name|getRemoveAllSubscriptionsStatment
parameter_list|()
block|{
return|return
literal|"DELETE FROM "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|" WHERE CONTAINER=?"
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
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE ( EXPIRATION<>0 AND EXPIRATION<?) OR ID<= "
operator|+
literal|"( SELECT min("
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|".LAST_ACKED_ID) "
operator|+
literal|"FROM "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|" WHERE "
operator|+
name|getTablePrefix
argument_list|()
operator|+
name|durableSubAcksTableName
operator|+
literal|".CONTAINER="
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|".CONTAINER)"
return|;
block|}
comment|/**      * @return Returns the containerNameDataType.      */
specifier|public
name|String
name|getContainerNameDataType
parameter_list|()
block|{
return|return
name|containerNameDataType
return|;
block|}
comment|/**      * @param containerNameDataType The containerNameDataType to set.      */
specifier|public
name|void
name|setContainerNameDataType
parameter_list|(
name|String
name|containerNameDataType
parameter_list|)
block|{
name|this
operator|.
name|containerNameDataType
operator|=
name|containerNameDataType
expr_stmt|;
block|}
comment|/**      * @return Returns the messageDataType.      */
specifier|public
name|String
name|getBinaryDataType
parameter_list|()
block|{
return|return
name|binaryDataType
return|;
block|}
comment|/**      * @param messageDataType The messageDataType to set.      */
specifier|public
name|void
name|setBinaryDataType
parameter_list|(
name|String
name|messageDataType
parameter_list|)
block|{
name|this
operator|.
name|binaryDataType
operator|=
name|messageDataType
expr_stmt|;
block|}
comment|/**      * @return Returns the messageTableName.      */
specifier|public
name|String
name|getMessageTableName
parameter_list|()
block|{
return|return
name|messageTableName
return|;
block|}
comment|/**      * @param messageTableName The messageTableName to set.      */
specifier|public
name|void
name|setMessageTableName
parameter_list|(
name|String
name|messageTableName
parameter_list|)
block|{
name|this
operator|.
name|messageTableName
operator|=
name|messageTableName
expr_stmt|;
block|}
comment|/**      * @return Returns the msgIdDataType.      */
specifier|public
name|String
name|getMsgIdDataType
parameter_list|()
block|{
return|return
name|msgIdDataType
return|;
block|}
comment|/**      * @param msgIdDataType The msgIdDataType to set.      */
specifier|public
name|void
name|setMsgIdDataType
parameter_list|(
name|String
name|msgIdDataType
parameter_list|)
block|{
name|this
operator|.
name|msgIdDataType
operator|=
name|msgIdDataType
expr_stmt|;
block|}
comment|/**      * @return Returns the sequenceDataType.      */
specifier|public
name|String
name|getSequenceDataType
parameter_list|()
block|{
return|return
name|sequenceDataType
return|;
block|}
comment|/**      * @param sequenceDataType The sequenceDataType to set.      */
specifier|public
name|void
name|setSequenceDataType
parameter_list|(
name|String
name|sequenceDataType
parameter_list|)
block|{
name|this
operator|.
name|sequenceDataType
operator|=
name|sequenceDataType
expr_stmt|;
block|}
comment|/**      * @return Returns the tablePrefix.      */
specifier|public
name|String
name|getTablePrefix
parameter_list|()
block|{
return|return
name|tablePrefix
return|;
block|}
comment|/**      * @param tablePrefix The tablePrefix to set.      */
specifier|public
name|void
name|setTablePrefix
parameter_list|(
name|String
name|tablePrefix
parameter_list|)
block|{
name|this
operator|.
name|tablePrefix
operator|=
name|tablePrefix
expr_stmt|;
block|}
comment|/**      * @return Returns the xidDataType.      */
specifier|public
name|String
name|getXidDataType
parameter_list|()
block|{
return|return
name|xidDataType
return|;
block|}
comment|/**      * @param xidDataType The xidDataType to set.      */
specifier|public
name|void
name|setXidDataType
parameter_list|(
name|String
name|xidDataType
parameter_list|)
block|{
name|this
operator|.
name|xidDataType
operator|=
name|xidDataType
expr_stmt|;
block|}
comment|/**      * @return Returns the durableSubAcksTableName.      */
specifier|public
name|String
name|getDurableSubAcksTableName
parameter_list|()
block|{
return|return
name|durableSubAcksTableName
return|;
block|}
comment|/**      * @param durableSubAcksTableName The durableSubAcksTableName to set.      */
specifier|public
name|void
name|setDurableSubAcksTableName
parameter_list|(
name|String
name|durableSubAcksTableName
parameter_list|)
block|{
name|this
operator|.
name|durableSubAcksTableName
operator|=
name|durableSubAcksTableName
expr_stmt|;
block|}
specifier|public
name|String
name|getLongDataType
parameter_list|()
block|{
return|return
name|longDataType
return|;
block|}
specifier|public
name|void
name|setLongDataType
parameter_list|(
name|String
name|longDataType
parameter_list|)
block|{
name|this
operator|.
name|longDataType
operator|=
name|longDataType
expr_stmt|;
block|}
specifier|public
name|String
name|getStringIdDataType
parameter_list|()
block|{
return|return
name|stringIdDataType
return|;
block|}
specifier|public
name|void
name|setStringIdDataType
parameter_list|(
name|String
name|stringIdDataType
parameter_list|)
block|{
name|this
operator|.
name|stringIdDataType
operator|=
name|stringIdDataType
expr_stmt|;
block|}
specifier|public
name|void
name|setUseExternalMessageReferences
parameter_list|(
name|boolean
name|useExternalMessageReferences
parameter_list|)
block|{
name|this
operator|.
name|useExternalMessageReferences
operator|=
name|useExternalMessageReferences
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseExternalMessageReferences
parameter_list|()
block|{
return|return
name|useExternalMessageReferences
return|;
block|}
block|}
end_class

end_unit

