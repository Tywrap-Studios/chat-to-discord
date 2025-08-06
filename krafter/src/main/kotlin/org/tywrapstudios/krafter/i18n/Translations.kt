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
