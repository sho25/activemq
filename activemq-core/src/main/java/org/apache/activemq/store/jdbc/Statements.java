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
package|;
end_package

begin_comment
comment|/**  * @version $Revision: 1.4 $  *   * @org.apache.xbean.XBean element="statements"  *   */
end_comment

begin_class
specifier|public
class|class
name|Statements
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
specifier|private
name|String
name|addMessageStatement
decl_stmt|;
specifier|private
name|String
name|updateMessageStatement
decl_stmt|;
specifier|private
name|String
name|removeMessageStatment
decl_stmt|;
specifier|private
name|String
name|findMessageSequenceIdStatement
decl_stmt|;
specifier|private
name|String
name|findMessageStatement
decl_stmt|;
specifier|private
name|String
name|findAllMessagesStatement
decl_stmt|;
specifier|private
name|String
name|findLastSequenceIdInMsgsStatement
decl_stmt|;
specifier|private
name|String
name|findLastSequenceIdInAcksStatement
decl_stmt|;
specifier|private
name|String
name|createDurableSubStatement
decl_stmt|;
specifier|private
name|String
name|findDurableSubStatement
decl_stmt|;
specifier|private
name|String
name|findAllDurableSubsStatement
decl_stmt|;
specifier|private
name|String
name|updateLastAckOfDurableSubStatement
decl_stmt|;
specifier|private
name|String
name|deleteSubscriptionStatement
decl_stmt|;
specifier|private
name|String
name|findAllDurableSubMessagesStatement
decl_stmt|;
specifier|private
name|String
name|findAllDestinationsStatement
decl_stmt|;
specifier|private
name|String
name|removeAllMessagesStatement
decl_stmt|;
specifier|private
name|String
name|removeAllSubscriptionsStatement
decl_stmt|;
specifier|private
name|String
name|deleteOldMessagesStatement
decl_stmt|;
specifier|private
name|String
index|[]
name|createSchemaStatements
decl_stmt|;
specifier|private
name|String
index|[]
name|dropSchemaStatements
decl_stmt|;
specifier|public
name|String
index|[]
name|getCreateSchemaStatements
parameter_list|()
block|{
if|if
condition|(
name|createSchemaStatements
operator|==
literal|null
condition|)
block|{
name|createSchemaStatements
operator|=
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
name|getFullAckTableName
argument_list|()
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
block|, }
expr_stmt|;
block|}
return|return
name|createSchemaStatements
return|;
block|}
specifier|public
name|String
index|[]
name|getDropSchemaStatements
parameter_list|()
block|{
if|if
condition|(
name|dropSchemaStatements
operator|==
literal|null
condition|)
block|{
name|dropSchemaStatements
operator|=
operator|new
name|String
index|[]
block|{
literal|"DROP TABLE "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|""
block|,
literal|"DROP TABLE "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|""
block|, }
expr_stmt|;
block|}
return|return
name|dropSchemaStatements
return|;
block|}
specifier|public
name|String
name|getAddMessageStatement
parameter_list|()
block|{
if|if
condition|(
name|addMessageStatement
operator|==
literal|null
condition|)
block|{
name|addMessageStatement
operator|=
literal|"INSERT INTO "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|"(ID, MSGID_PROD, MSGID_SEQ, CONTAINER, EXPIRATION, MSG) VALUES (?, ?, ?, ?, ?, ?)"
expr_stmt|;
block|}
return|return
name|addMessageStatement
return|;
block|}
specifier|public
name|String
name|getUpdateMessageStatement
parameter_list|()
block|{
if|if
condition|(
name|updateMessageStatement
operator|==
literal|null
condition|)
block|{
name|updateMessageStatement
operator|=
literal|"UPDATE "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" SET MSG=? WHERE ID=?"
expr_stmt|;
block|}
return|return
name|updateMessageStatement
return|;
block|}
specifier|public
name|String
name|getRemoveMessageStatment
parameter_list|()
block|{
if|if
condition|(
name|removeMessageStatment
operator|==
literal|null
condition|)
block|{
name|removeMessageStatment
operator|=
literal|"DELETE FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE ID=?"
expr_stmt|;
block|}
return|return
name|removeMessageStatment
return|;
block|}
specifier|public
name|String
name|getFindMessageSequenceIdStatement
parameter_list|()
block|{
if|if
condition|(
name|findMessageSequenceIdStatement
operator|==
literal|null
condition|)
block|{
name|findMessageSequenceIdStatement
operator|=
literal|"SELECT ID FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE MSGID_PROD=? AND MSGID_SEQ=?"
expr_stmt|;
block|}
return|return
name|findMessageSequenceIdStatement
return|;
block|}
specifier|public
name|String
name|getFindMessageStatement
parameter_list|()
block|{
if|if
condition|(
name|findMessageStatement
operator|==
literal|null
condition|)
block|{
name|findMessageStatement
operator|=
literal|"SELECT MSG FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE ID=?"
expr_stmt|;
block|}
return|return
name|findMessageStatement
return|;
block|}
specifier|public
name|String
name|getFindAllMessagesStatement
parameter_list|()
block|{
if|if
condition|(
name|findAllMessagesStatement
operator|==
literal|null
condition|)
block|{
name|findAllMessagesStatement
operator|=
literal|"SELECT ID, MSG FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=? ORDER BY ID"
expr_stmt|;
block|}
return|return
name|findAllMessagesStatement
return|;
block|}
specifier|public
name|String
name|getFindLastSequenceIdInMsgsStatement
parameter_list|()
block|{
if|if
condition|(
name|findLastSequenceIdInMsgsStatement
operator|==
literal|null
condition|)
block|{
name|findLastSequenceIdInMsgsStatement
operator|=
literal|"SELECT MAX(ID) FROM "
operator|+
name|getFullMessageTableName
argument_list|()
expr_stmt|;
block|}
return|return
name|findLastSequenceIdInMsgsStatement
return|;
block|}
specifier|public
name|String
name|getFindLastSequenceIdInAcksStatement
parameter_list|()
block|{
if|if
condition|(
name|findLastSequenceIdInAcksStatement
operator|==
literal|null
condition|)
block|{
name|findLastSequenceIdInAcksStatement
operator|=
literal|"SELECT MAX(LAST_ACKED_ID) FROM "
operator|+
name|getFullAckTableName
argument_list|()
expr_stmt|;
block|}
return|return
name|findLastSequenceIdInAcksStatement
return|;
block|}
specifier|public
name|String
name|getCreateDurableSubStatement
parameter_list|()
block|{
if|if
condition|(
name|createDurableSubStatement
operator|==
literal|null
condition|)
block|{
name|createDurableSubStatement
operator|=
literal|"INSERT INTO "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|"(CONTAINER, CLIENT_ID, SUB_NAME, SELECTOR, LAST_ACKED_ID) "
operator|+
literal|"VALUES (?, ?, ?, ?, ?)"
expr_stmt|;
block|}
return|return
name|createDurableSubStatement
return|;
block|}
specifier|public
name|String
name|getFindDurableSubStatement
parameter_list|()
block|{
if|if
condition|(
name|findDurableSubStatement
operator|==
literal|null
condition|)
block|{
name|findDurableSubStatement
operator|=
literal|"SELECT SELECTOR, SUB_NAME "
operator|+
literal|"FROM "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?"
expr_stmt|;
block|}
return|return
name|findDurableSubStatement
return|;
block|}
specifier|public
name|String
name|getFindAllDurableSubsStatement
parameter_list|()
block|{
if|if
condition|(
name|findAllDurableSubsStatement
operator|==
literal|null
condition|)
block|{
name|findAllDurableSubsStatement
operator|=
literal|"SELECT SELECTOR, SUB_NAME, CLIENT_ID"
operator|+
literal|" FROM "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=?"
expr_stmt|;
block|}
return|return
name|findAllDurableSubsStatement
return|;
block|}
specifier|public
name|String
name|getUpdateLastAckOfDurableSubStatement
parameter_list|()
block|{
if|if
condition|(
name|updateLastAckOfDurableSubStatement
operator|==
literal|null
condition|)
block|{
name|updateLastAckOfDurableSubStatement
operator|=
literal|"UPDATE "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|" SET LAST_ACKED_ID=?"
operator|+
literal|" WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?"
expr_stmt|;
block|}
return|return
name|updateLastAckOfDurableSubStatement
return|;
block|}
specifier|public
name|String
name|getDeleteSubscriptionStatement
parameter_list|()
block|{
if|if
condition|(
name|deleteSubscriptionStatement
operator|==
literal|null
condition|)
block|{
name|deleteSubscriptionStatement
operator|=
literal|"DELETE FROM "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=? AND CLIENT_ID=? AND SUB_NAME=?"
expr_stmt|;
block|}
return|return
name|deleteSubscriptionStatement
return|;
block|}
specifier|public
name|String
name|getFindAllDurableSubMessagesStatement
parameter_list|()
block|{
if|if
condition|(
name|findAllDurableSubMessagesStatement
operator|==
literal|null
condition|)
block|{
name|findAllDurableSubMessagesStatement
operator|=
literal|"SELECT M.ID, M.MSG FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" M, "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|" D "
operator|+
literal|" WHERE D.CONTAINER=? AND D.CLIENT_ID=? AND D.SUB_NAME=?"
operator|+
literal|" AND M.CONTAINER=D.CONTAINER AND M.ID> D.LAST_ACKED_ID"
operator|+
literal|" ORDER BY M.ID"
expr_stmt|;
block|}
return|return
name|findAllDurableSubMessagesStatement
return|;
block|}
specifier|public
name|String
name|getFindAllDestinationsStatement
parameter_list|()
block|{
if|if
condition|(
name|findAllDestinationsStatement
operator|==
literal|null
condition|)
block|{
name|findAllDestinationsStatement
operator|=
literal|"SELECT DISTINCT CONTAINER FROM "
operator|+
name|getFullMessageTableName
argument_list|()
expr_stmt|;
block|}
return|return
name|findAllDestinationsStatement
return|;
block|}
specifier|public
name|String
name|getRemoveAllMessagesStatement
parameter_list|()
block|{
if|if
condition|(
name|removeAllMessagesStatement
operator|==
literal|null
condition|)
block|{
name|removeAllMessagesStatement
operator|=
literal|"DELETE FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=?"
expr_stmt|;
block|}
return|return
name|removeAllMessagesStatement
return|;
block|}
specifier|public
name|String
name|getRemoveAllSubscriptionsStatement
parameter_list|()
block|{
if|if
condition|(
name|removeAllSubscriptionsStatement
operator|==
literal|null
condition|)
block|{
name|removeAllSubscriptionsStatement
operator|=
literal|"DELETE FROM "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|" WHERE CONTAINER=?"
expr_stmt|;
block|}
return|return
name|removeAllSubscriptionsStatement
return|;
block|}
specifier|public
name|String
name|getDeleteOldMessagesStatement
parameter_list|()
block|{
if|if
condition|(
name|deleteOldMessagesStatement
operator|==
literal|null
condition|)
block|{
name|deleteOldMessagesStatement
operator|=
literal|"DELETE FROM "
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|" WHERE ( EXPIRATION<>0 AND EXPIRATION<?) OR ID<= "
operator|+
literal|"( SELECT min("
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|".LAST_ACKED_ID) "
operator|+
literal|"FROM "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|" WHERE "
operator|+
name|getFullAckTableName
argument_list|()
operator|+
literal|".CONTAINER="
operator|+
name|getFullMessageTableName
argument_list|()
operator|+
literal|".CONTAINER)"
expr_stmt|;
block|}
return|return
name|deleteOldMessagesStatement
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
name|getMessageTableName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getFullAckTableName
parameter_list|()
block|{
return|return
name|getTablePrefix
argument_list|()
operator|+
name|getDurableSubAcksTableName
argument_list|()
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
comment|/**      * @param containerNameDataType      *            The containerNameDataType to set.      */
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
comment|/**      * @param messageDataType      *            The messageDataType to set.      */
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
comment|/**      * @param messageTableName      *            The messageTableName to set.      */
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
comment|/**      * @param msgIdDataType      *            The msgIdDataType to set.      */
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
comment|/**      * @param sequenceDataType      *            The sequenceDataType to set.      */
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
comment|/**      * @param tablePrefix      *            The tablePrefix to set.      */
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
comment|/**      * @param durableSubAcksTableName      *            The durableSubAcksTableName to set.      */
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
specifier|public
name|void
name|setAddMessageStatement
parameter_list|(
name|String
name|addMessageStatment
parameter_list|)
block|{
name|this
operator|.
name|addMessageStatement
operator|=
name|addMessageStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setCreateDurableSubStatement
parameter_list|(
name|String
name|createDurableSubStatment
parameter_list|)
block|{
name|this
operator|.
name|createDurableSubStatement
operator|=
name|createDurableSubStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setCreateSchemaStatements
parameter_list|(
name|String
index|[]
name|createSchemaStatments
parameter_list|)
block|{
name|this
operator|.
name|createSchemaStatements
operator|=
name|createSchemaStatments
expr_stmt|;
block|}
specifier|public
name|void
name|setDeleteOldMessagesStatement
parameter_list|(
name|String
name|deleteOldMessagesStatment
parameter_list|)
block|{
name|this
operator|.
name|deleteOldMessagesStatement
operator|=
name|deleteOldMessagesStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setDeleteSubscriptionStatement
parameter_list|(
name|String
name|deleteSubscriptionStatment
parameter_list|)
block|{
name|this
operator|.
name|deleteSubscriptionStatement
operator|=
name|deleteSubscriptionStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setDropSchemaStatements
parameter_list|(
name|String
index|[]
name|dropSchemaStatments
parameter_list|)
block|{
name|this
operator|.
name|dropSchemaStatements
operator|=
name|dropSchemaStatments
expr_stmt|;
block|}
specifier|public
name|void
name|setFindAllDestinationsStatement
parameter_list|(
name|String
name|findAllDestinationsStatment
parameter_list|)
block|{
name|this
operator|.
name|findAllDestinationsStatement
operator|=
name|findAllDestinationsStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setFindAllDurableSubMessagesStatement
parameter_list|(
name|String
name|findAllDurableSubMessagesStatment
parameter_list|)
block|{
name|this
operator|.
name|findAllDurableSubMessagesStatement
operator|=
name|findAllDurableSubMessagesStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setFindAllDurableSubsStatement
parameter_list|(
name|String
name|findAllDurableSubsStatment
parameter_list|)
block|{
name|this
operator|.
name|findAllDurableSubsStatement
operator|=
name|findAllDurableSubsStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setFindAllMessagesStatement
parameter_list|(
name|String
name|findAllMessagesStatment
parameter_list|)
block|{
name|this
operator|.
name|findAllMessagesStatement
operator|=
name|findAllMessagesStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setFindDurableSubStatement
parameter_list|(
name|String
name|findDurableSubStatment
parameter_list|)
block|{
name|this
operator|.
name|findDurableSubStatement
operator|=
name|findDurableSubStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setFindLastSequenceIdInAcksStatement
parameter_list|(
name|String
name|findLastSequenceIdInAcks
parameter_list|)
block|{
name|this
operator|.
name|findLastSequenceIdInAcksStatement
operator|=
name|findLastSequenceIdInAcks
expr_stmt|;
block|}
specifier|public
name|void
name|setFindLastSequenceIdInMsgsStatement
parameter_list|(
name|String
name|findLastSequenceIdInMsgs
parameter_list|)
block|{
name|this
operator|.
name|findLastSequenceIdInMsgsStatement
operator|=
name|findLastSequenceIdInMsgs
expr_stmt|;
block|}
specifier|public
name|void
name|setFindMessageSequenceIdStatement
parameter_list|(
name|String
name|findMessageSequenceIdStatment
parameter_list|)
block|{
name|this
operator|.
name|findMessageSequenceIdStatement
operator|=
name|findMessageSequenceIdStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setFindMessageStatement
parameter_list|(
name|String
name|findMessageStatment
parameter_list|)
block|{
name|this
operator|.
name|findMessageStatement
operator|=
name|findMessageStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setRemoveAllMessagesStatement
parameter_list|(
name|String
name|removeAllMessagesStatment
parameter_list|)
block|{
name|this
operator|.
name|removeAllMessagesStatement
operator|=
name|removeAllMessagesStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setRemoveAllSubscriptionsStatement
parameter_list|(
name|String
name|removeAllSubscriptionsStatment
parameter_list|)
block|{
name|this
operator|.
name|removeAllSubscriptionsStatement
operator|=
name|removeAllSubscriptionsStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setRemoveMessageStatment
parameter_list|(
name|String
name|removeMessageStatment
parameter_list|)
block|{
name|this
operator|.
name|removeMessageStatment
operator|=
name|removeMessageStatment
expr_stmt|;
block|}
specifier|public
name|void
name|setUpdateLastAckOfDurableSubStatement
parameter_list|(
name|String
name|updateLastAckOfDurableSub
parameter_list|)
block|{
name|this
operator|.
name|updateLastAckOfDurableSubStatement
operator|=
name|updateLastAckOfDurableSub
expr_stmt|;
block|}
specifier|public
name|void
name|setUpdateMessageStatement
parameter_list|(
name|String
name|updateMessageStatment
parameter_list|)
block|{
name|this
operator|.
name|updateMessageStatement
operator|=
name|updateMessageStatment
expr_stmt|;
block|}
block|}
end_class

end_unit

