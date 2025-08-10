package org.tywrapstudios.krafter.i18n

import dev.kordex.core.i18n.types.Bundle
import dev.kordex.core.i18n.types.Key

public object Translations {
  public val bundle: Bundle = Bundle("krafter.strings")

  public object Checks {
    public object HasId {
      /**
       * Must have id: **{0}**
       */
      public val failed: Key = Key("checks.hasId.failed")
          .withBundle(Translations.bundle)
    }

    public object IsBotModuleAdmin {
      /**
       * Must be bot module admin for **{0}**
       */
      public val failed: Key = Key("checks.isBotModuleAdmin.failed")
          .withBundle(Translations.bundle)
    }

    public object NotHasId {
      /**
       * Must not have id: **{0}**
       */
      public val failed: Key = Key("checks.notHasId.failed")
          .withBundle(Translations.bundle)
    }

    public object NotIsBotModuleAdmin {
      /**
       * Must not be bot module admin for **{0}**
       */
      public val failed: Key = Key("checks.notIsBotModuleAdmin.failed")
          .withBundle(Translations.bundle)
    }
  }

  public object Commands {
    public object Minecraft {
      /**
       * force-link
       */
      public val forceLink: Key = Key("commands.minecraft.forceLink")
          .withBundle(Translations.bundle)

      /**
       * link
       */
      public val link: Key = Key("commands.minecraft.link")
          .withBundle(Translations.bundle)

      /**
       * unlink
       */
      public val unlink: Key = Key("commands.minecraft.unlink")
          .withBundle(Translations.bundle)

      public object ForceLink {
        /**
         * Force link a Minecraft account to a Discord account.
         */
        public val description: Key = Key("commands.minecraft.forceLink.description")
            .withBundle(Translations.bundle)

        /**
         * Successfully linked {0} to Minecraft account with uuid {1}.
         */
        public val success: Key = Key("commands.minecraft.forceLink.success")
            .withBundle(Translations.bundle)

        public object Arg {
          /**
           * user
           */
          public val member: Key = Key("commands.minecraft.forceLink.arg.member")
              .withBundle(Translations.bundle)

          /**
           * uuid
           */
          public val uuid: Key = Key("commands.minecraft.forceLink.arg.uuid")
              .withBundle(Translations.bundle)

          public object Member {
            /**
             * The member to link the Minecraft account to.
             */
            public val description: Key = Key("commands.minecraft.forceLink.arg.member.description")
                .withBundle(Translations.bundle)
          }

          public object Uuid {
            /**
             * The UUID of the Minecraft account to link.
             */
            public val description: Key = Key("commands.minecraft.forceLink.arg.uuid.description")
                .withBundle(Translations.bundle)
          }
        }

        public object Error {
          /**
           * The specified user already has a verified linked Minecraft account with uuid {0}.
           */
          public val alreadyLinked: Key = Key("commands.minecraft.forceLink.error.alreadyLinked")
              .withBundle(Translations.bundle)

          /**
           * The specified user already has a verified linked Minecraft account with a different
           * uuid: {0}. Please unlink it first.
           */
          public val alreadyLinkedDifferent: Key =
              Key("commands.minecraft.forceLink.error.alreadyLinkedDifferent")
              .withBundle(Translations.bundle)

          /**
           * Force linking Minecraft accounts is currently disabled.
           */
          public val disabled: Key = Key("commands.minecraft.forceLink.error.disabled")
              .withBundle(Translations.bundle)

          /**
           * The provided UUID is invalid. Please provide a valid Minecraft UUID.
           */
          public val invalidUuid: Key = Key("commands.minecraft.forceLink.error.invalidUuid")
              .withBundle(Translations.bundle)
        }
      }

      public object Link {
        /**
         * Link your Minecraft account to your Discord account.
         */
        public val description: Key = Key("commands.minecraft.link.description")
            .withBundle(Translations.bundle)

        /**
         * Please join the Minecraft server and run the command `/ctd-krafter link {0}` to complete
         * the linking process.
         */
        public val success: Key = Key("commands.minecraft.link.success")
            .withBundle(Translations.bundle)

        public object Arg {
          /**
           * uuid
           */
          public val uuid: Key = Key("commands.minecraft.link.arg.uuid")
              .withBundle(Translations.bundle)

          public object Uuid {
            /**
             * The UUID of the Minecraft account you want to link.
             */
            public val description: Key = Key("commands.minecraft.link.arg.uuid.description")
                .withBundle(Translations.bundle)
          }
        }

        public object Error {
          /**
           * Linking Minecraft accounts is currently disabled.
           */
          public val disabled: Key = Key("commands.minecraft.link.error.disabled")
              .withBundle(Translations.bundle)

          /**
           * The provided UUID is invalid. Please provide a valid Minecraft UUID.
           */
          public val invalidUuid: Key = Key("commands.minecraft.link.error.invalidUuid")
              .withBundle(Translations.bundle)
        }
      }

      public object Unlink {
        /**
         * Unlink your Minecraft account from your Discord account.
         */
        public val description: Key = Key("commands.minecraft.unlink.description")
            .withBundle(Translations.bundle)

        /**
         * Your Minecraft account with uuid {0} has successfully been unlinked.
         */
        public val success: Key = Key("commands.minecraft.unlink.success")
            .withBundle(Translations.bundle)

        public object Error {
          /**
           * Unlinking Minecraft accounts is currently disabled.
           */
          public val disabled: Key = Key("commands.minecraft.unlink.error.disabled")
              .withBundle(Translations.bundle)

          /**
           * Your Discord account is not linked to any Minecraft account.
           */
          public val notLinked: Key = Key("commands.minecraft.unlink.error.notLinked")
              .withBundle(Translations.bundle)
        }
      }
    }
  }

  public object Enum {
    public object SuggestionStatus {
      /**
       * Approved
       */
      public val approved: Key = Key("enum.suggestionStatus.approved")
          .withBundle(Translations.bundle)

      /**
       * Denied
       */
      public val denied: Key = Key("enum.suggestionStatus.denied")
          .withBundle(Translations.bundle)

      /**
       * Duplicate
       */
      public val duplicate: Key = Key("enum.suggestionStatus.duplicate")
          .withBundle(Translations.bundle)

      /**
       * Future Concern
       */
      public val future: Key = Key("enum.suggestionStatus.future")
          .withBundle(Translations.bundle)

      /**
       * Implemented
       */
      public val implemented: Key = Key("enum.suggestionStatus.implemented")
          .withBundle(Translations.bundle)

      /**
       * Invalid
       */
      public val invalid: Key = Key("enum.suggestionStatus.invalid")
          .withBundle(Translations.bundle)

      /**
       * Open
       */
      public val `open`: Key = Key("enum.suggestionStatus.open")
          .withBundle(Translations.bundle)

      /**
       * Requires Name
       */
      public val requiresName: Key = Key("enum.suggestionStatus.requires_name")
          .withBundle(Translations.bundle)

      /**
       * Spam
       */
      public val spam: Key = Key("enum.suggestionStatus.spam")
          .withBundle(Translations.bundle)

      /**
       * Stale
       */
      public val stale: Key = Key("enum.suggestionStatus.stale")
          .withBundle(Translations.bundle)
    }
  }

  public object Extensions {
    public object Sab {
      /**
       * {0} Safety and Abuse
       */
      public val footer: Key = Key("extensions.sab.footer")
          .withBundle(Translations.bundle)

      /**
       * Safety and Abuse
       */
      public val name: Key = Key("extensions.sab.name")
          .withBundle(Translations.bundle)
    }
  }
}
